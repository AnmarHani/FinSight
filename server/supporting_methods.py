from flask import Flask, request, jsonify
import requests
import mysql.connector
from mysql.connector import Error
from pydantic import BaseModel
import bcrypt
from jose import JWTError, jwt
from typing import Optional
from decimal import Decimal
from datetime import datetime
from supporting_methods import *
import plotly.graph_objects as go
import time
from pydantic import BaseModel, ValidationError
from constants import DB_HOST, DB_USER, DB_PASS, DB_NAME


SECRET_KEY = "222"







def generate_gemini_content(prompt_text, api_key):
    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key={api_key}"
    headers = {
        "Content-Type": "application/json"
    }
    data = {
        "contents": [
            {
                "parts": [
                    {
                        "text": prompt_text
                    }
                ]
            }
        ]
    }
    response = requests.post(url, json=data, headers=headers)
    if response.status_code == 200:
        try:
            response_json = response.json()
            generated_text = response_json["candidates"][0]["content"]["parts"][0]["text"]
            return generated_text
        except Exception as e:
            print("Error parsing JSON response:", e)
            return None
    else:
        print("Error:", response.status_code)
        return None
    
# Define your MySQL connection parameters
db_config = {
    'host': DB_HOST,
    'database': DB_NAME,
    'user': DB_USER,
    'password': DB_PASS
}

while True:
    try:
        
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor()


        create_user_table_query = """
        CREATE TABLE IF NOT EXISTS `user` (
            `id` INT AUTO_INCREMENT PRIMARY KEY,
            `username` VARCHAR(255) UNIQUE NOT NULL,
            `email` VARCHAR(255) UNIQUE NOT NULL,
            `password` VARCHAR(255) NOT NULL,
            `current_balance` DECIMAL(10, 2) DEFAULT 0,
            `monthly_income` DECIMAL(10, 2),
            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
        """

        create_transaction_table_query = """
        CREATE TABLE IF NOT EXISTS `transaction` (
            `id` INT AUTO_INCREMENT PRIMARY KEY,
            `user_id` INT NOT NULL,
            `amount` DECIMAL(10, 2) NOT NULL,
            `transaction_date` DATE NOT NULL,
            `description` VARCHAR(255),
            `transaction_type` INT NOT NULL,
            FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
        )
        """

        create_budget_table="""CREATE TABLE IF NOT EXISTS budget (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            budget_amount DECIMAL(10, 2) NOT NULL,
            budget_name VARCHAR(255) NOT NULL,
            budget_type VARCHAR(255) NOT NULL,
            FOREIGN KEY (user_id) REFERENCES user(id)
        )
        """

        cursor.execute(create_user_table_query)
        cursor.execute(create_transaction_table_query)
        cursor.execute(create_budget_table)
        connection.commit()
        print("Tables created successfully!")

        
        cursor.close()
        connection.close()

        break

    except mysql.connector.Error as err:
        print(f"Error: {err}")
        print("Retrying in 5 seconds...")
        time.sleep(5)


class UserRegistration(BaseModel):
    username: str
    email: str
    password: str
    current_balance: Optional[Decimal] = None
    monthly_income: Optional[Decimal] = None


class UserLogin(BaseModel):
    username: str
    password: str

class TransactionRequest(BaseModel):
    amount: float
    description: str
    transaction_type: int


class CreateBudgetRequest(BaseModel):
    budget_amount: float
    budget_name: str
    budget_type: str

def hash_password(password):
    salt = bcrypt.gensalt()
    hashed_password = bcrypt.hashpw(password.encode('utf-8'), salt)
    return hashed_password.decode('utf-8')

def verify_password(plain_password, hashed_password):
    return bcrypt.checkpw(plain_password.encode('utf-8'), hashed_password.encode('utf-8'))


def get_user_by_username(username):
    try:
        connection = mysql.connector.connect(**db_config)
        if connection.is_connected():
            cursor = connection.cursor(dictionary=True)
            query = "SELECT * FROM user WHERE username = %s"
            cursor.execute(query, (username,))
            user = cursor.fetchone()
            return user
    except Error as e:
        print("Error while connecting to MySQL", e)
        return None
    finally:
        if (connection.is_connected()):
            cursor.close()
            connection.close()


def add_transaction_to_database(conn, user_id, amount, transaction_date, description, transaction_type):
    try:
       
        cursor = conn.cursor()

        
        cursor.execute("""
            INSERT INTO transaction (user_id, amount, transaction_date, description, transaction_type)
            VALUES (%s, %s, %s, %s, %s)
        """, (user_id, amount, transaction_date, description, transaction_type))
        conn.commit()

       
        if transaction_type == 1:
            cursor.execute("""
                UPDATE user
                SET current_balance = current_balance + %s
                WHERE id = %s
            """, (amount, user_id))
        elif transaction_type == -1:
            cursor.execute("""
                UPDATE user
                SET current_balance = current_balance - %s
                WHERE id = %s
            """, (amount, user_id))
        conn.commit()

       
        cursor.close()

    except mysql.connector.Error as err:
        print("Error:", err)
        raise




def generate_token(username):
    payload = {'username': username}
    token = jwt.encode(payload, SECRET_KEY, algorithm='HS256')
    print("Generated token:", token)  
    return token

def decode_token(token):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=['HS256'])
        print("Decoded payload:", payload)  
        return payload
    except jwt.ExpiredSignatureError:
        print("Token expired.")  
        return {'error': 'Token expired.'}
    except jwt.InvalidTokenError:
        print("Invalid token.")  
        return {'error': 'Invalid token.'}



def username_exists(username):
    try:
        connection = mysql.connector.connect(**db_config)
        if connection.is_connected():
            cursor = connection.cursor()
            query = "SELECT username FROM user WHERE username = %s"
            cursor.execute(query, (username,))
            result = cursor.fetchone()
            return bool(result)
    except Error as e:
        print("Error while connecting to MySQL", e)
        return False
    finally:
        if (connection.is_connected()):
            cursor.close()
            connection.close()


def insert_user(username, email, password, current_balance=None, monthly_income=None):
    try:
        connection = mysql.connector.connect(**db_config)
        if connection.is_connected():
            cursor = connection.cursor()
            hashed_password = hash_password(password)
            query = "INSERT INTO user (username, email, password, current_balance, monthly_income) VALUES (%s, %s, %s, %s, %s)"
            cursor.execute(query, (username, email, hashed_password, current_balance, monthly_income))
            connection.commit()
            return True
    except Error as e:
        print("Error while connecting to MySQL", e)
        return False
    finally:
        if (connection.is_connected()):
            cursor.close()
            connection.close()
def get_user_transactions(user_id):
    # Establish MySQL connection

    cursor = connection.cursor(dictionary=True)
    # Retrieve user's transactions from the database
    sql = "SELECT description, amount FROM transaction WHERE user_id = %s"
    cursor.execute(sql, (user_id,))
    transactions = cursor.fetchall()

    # Close the connection
    cursor.close()
    connection.close()

    return transactions


def generate_transaction_graph(transactions):
    categories = {}
    total_amount = 0

    # Calculate total amount and group transactions by category
    for transaction in transactions:
        total_amount += transaction['amount']
        category = categories.get(transaction['description'], 0)
        categories[transaction['description']] = category + transaction['amount']

    # Calculate percentage for each category
    category_percentages = {category: (amount / total_amount) * 100 for category, amount in categories.items()}

    # Create the pie chart
    labels = list(category_percentages.keys())
    values = list(category_percentages.values())

    fig = go.Figure(data=[go.Pie(labels=labels, values=values)])
    fig.update_layout(title_text="Transaction Analysis")

    return fig.to_html(full_html=False, include_plotlyjs='cdn')
def get_user_id_by_username(username):
    try:
        # Connect to the database
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor()

        # Execute the query to retrieve user_id based on username
        query = """
        SELECT id FROM user WHERE username = %s
        """
        cursor.execute(query, (username,))
        result = cursor.fetchone()

        if result:
            # Extract and return the user_id
            user_id = result[0]
            return user_id
        else:
            # If user is not found, return None
            return None
    except mysql.connector.Error as err:
        print("Error:", err)
        return None
    finally:
        # Close cursor and connection
        cursor.close()
        connection.close()

def generate_transaction_graph(transactions):
    categories = {}
    total_amount = 0

    # Calculate total amount and group transactions by category
    for transaction in transactions:
        total_amount += transaction['amount']
        category = categories.get(transaction['description'], 0)
        categories[transaction['description']] = category + transaction['amount']

    # Calculate percentage for each category
    category_percentages = {category: (amount / total_amount) * 100 for category, amount in categories.items()}

    # Create the pie chart
    labels = list(category_percentages.keys())
    values = list(category_percentages.values())

    fig = go.Figure(data=[go.Pie(labels=labels, values=values)])
    fig.update_layout(title_text="Transaction Analysis")

    return fig.to_html(full_html=False, include_plotlyjs='cdn')





finance_keywords = ["finance", "money", "investment", "budget", "income", "savings", "expenses"]