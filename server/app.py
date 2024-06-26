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
from pydantic import BaseModel, ValidationError
import uuid
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from constants import APP_HOST, APP_PORT

app = Flask(__name__)

last_transactions = []
budgets = []

@app.route('/', methods=['GET'])
def root():
    return "Hello World"

@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    user_data = UserRegistration(**data)
    
    if username_exists(user_data.username):
        return jsonify({'error': 'Username already exists!'}), 409

    if insert_user(user_data.username, user_data.email, user_data.password, user_data.current_balance, user_data.monthly_income):
        return jsonify({'message': 'User registered successfully!'}), 201
    else:
        return jsonify({'error': 'Failed to register user!'}), 500



@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    user_data = UserLogin(**data)

    user = get_user_by_username(user_data.username)

    if user and verify_password(user_data.password, user['password']):
        token = generate_token(user_data.username)
        return jsonify({'token': token}), 200
    else:
        return jsonify({'error': 'Invalid username or password.'}), 401


@app.route('/generate_gemini_content', methods=['GET'])
def generate_gemini_content_route():
    jwt_token = request.headers.get('Authorization')
    
    if not jwt_token:
        return jsonify({"error": "JWT token is missing."}), 400
    
    if jwt_token.startswith('Bearer '):
        jwt_token = jwt_token[7:]
    
    try:
        decoded_token = jwt.decode(jwt_token, SECRET_KEY, algorithms=['HS256'])
        username = decoded_token.get('username')
        
        user = get_user_by_username(username)
        if not user:
            return jsonify({"error": "User not found."}), 404

    
        prompt_text = f"""
        I am a user with a monthly income of {user['monthly_income']} and a current balance of {user['current_balance']}.
        My List of transactions to my account {str(last_transactions)}. Also, my budgets that I did are {str(budgets)}. NOTE: [IF EMPTY LISTS PLEASE IGNORE].
        Give me (Personalised based on my numbers) suggestions and different strategies to increase my income, save money, and any financial improvement and management in general.
        """
    
    except jwt.ExpiredSignatureError:
        return jsonify({"error": "Expired token."}), 401
    except jwt.InvalidTokenError:
        return jsonify({"error": "Invalid token."}), 401
    
    api_key = "AIzaSyAyA2yr5nQv2QAdI1c6W1SMKyHZsYD3sxo"  
    try:
        generated_text = generate_gemini_content(prompt_text, api_key)
        return jsonify({"generated_text": generated_text})
    except Exception as e:
        return jsonify({"error": "Failed to generate content.", "details": str(e)}), 500


@app.route('/add_transaction', methods=['POST'])
def add_transaction():
    jwt_token = request.headers.get('Authorization')
    
    if not jwt_token:
        return jsonify({"error": "JWT token is missing."}), 400
    
    if jwt_token.startswith('Bearer '):
        jwt_token = jwt_token[7:]
    
    try:
        decoded_token = jwt.decode(jwt_token, SECRET_KEY, algorithms=['HS256'])
        username = decoded_token.get('username')

        user = get_user_by_username(username)
        
        if user:
            data = request.json
            transaction_request = TransactionRequest(**data)

            # 1 is postive transaction and -1 is negative transaction
            if transaction_request.transaction_type not in [-1, 1]:
                return jsonify({"error": "Invalid transaction type."}), 400

      
            amount = Decimal(transaction_request.amount)

           
            transaction_date = datetime.now() 
            
     
            conn = mysql.connector.connect(**db_config)

      
            add_transaction_to_database(conn, user['id'], amount, transaction_date, transaction_request.description, transaction_request.transaction_type)

            
            if transaction_request.transaction_type == 1:
                user['current_balance'] += amount
            elif transaction_request.transaction_type == -1:
                user['current_balance'] -= amount

        
            conn.close()

            return jsonify({"message": "Transaction added successfully.", "current_balance": str(user['current_balance'])}), 200
        else:
            return jsonify({"error": "User not found."}), 404
    except jwt.ExpiredSignatureError:
        return jsonify({"error": "Expired token."}), 401
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    


@app.route('/user_transactions', methods=['GET'])
def get_user_transactions():
    global last_transactions

    jwt_token = request.headers.get('Authorization')

    if not jwt_token:
        return jsonify({"error": "JWT token is missing."}), 400
    
    if jwt_token.startswith('Bearer '):
        jwt_token = jwt_token[7:]


    try:
        decoded_token = jwt.decode(jwt_token, SECRET_KEY, algorithms=['HS256'])

        username = decoded_token.get('username')

    except jwt.InvalidTokenError:
        return jsonify({"error": "Invalid JWT token."}), 400

    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)

        query = """
        SELECT t.id AS transaction_id, t.amount, t.transaction_date, t.description, t.transaction_type
        FROM user u
        JOIN transaction t ON u.id = t.user_id
        WHERE u.username = %s
        """
        cursor.execute(query, (username,))

        user_transactions = cursor.fetchall()

        last_transactions = user_transactions

        cursor.close()
        connection.close()

        return jsonify({"result": user_transactions}), 200
    except mysql.connector.Error as err:
        return jsonify({"error": str(err)}), 500


@app.route('/create_budget', methods=['POST'])
def create_budget():
    jwt_token = request.headers.get('Authorization')
    if not jwt_token:
        return jsonify({"error": "JWT token is missing."}), 400
    
    if jwt_token.startswith('Bearer '):
        jwt_token = jwt_token[7:]
    
    try:
        decoded_token = jwt.decode(jwt_token, SECRET_KEY, algorithms=['HS256'])
        username = decoded_token.get('username')
    except jwt.ExpiredSignatureError:
        return jsonify({"error": "Expired token."}), 401
    except jwt.InvalidTokenError:
        return jsonify({"error": "Invalid token."}), 401
    
  
    user_id = get_user_id_by_username(username)
    if not user_id:
        return jsonify({"error": "User not found."}), 404
    
  
    try:
        request_data = request.json
        budget_request = CreateBudgetRequest(**request_data)
    except ValidationError as e:
        return jsonify({"error": e.errors()}), 400
    

    try:
        # Connect to the database
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor()

       
        insert_query = """
        INSERT INTO budget (user_id, budget_amount, budget_name, budget_type)
        VALUES (%s, %s, %s, %s)
        """
        cursor.execute(insert_query, (user_id, budget_request.budget_amount, budget_request.budget_name, budget_request.budget_type))
        connection.commit()

        
        cursor.close()
        connection.close()

        return jsonify({"message": "Budget created successfully."}), 201
    except mysql.connector.Error as err:
        return jsonify({"error": str(err)}), 500

@app.route('/update_user', methods=['PUT'])
def update_user():
    jwt_token = request.headers.get('Authorization')
    if not jwt_token:
        return jsonify({"error": "JWT token is missing."}), 400

    if jwt_token.startswith('Bearer '):
        jwt_token = jwt_token[7:]

    try:
        decoded_token = jwt.decode(jwt_token, SECRET_KEY, algorithms=['HS256'])
        username = decoded_token.get('username')
    except jwt.ExpiredSignatureError:
        return jsonify({"error": "Expired token."}), 401
    except jwt.InvalidTokenError:
        return jsonify({"error": "Invalid token."}), 401

    user_id = get_user_id_by_username(username)
    if not user_id:
        return jsonify({"error": "User not found."}), 404

    request_data = request.json
    current_balance = request_data.get('current_balance')
    monthly_income = request_data.get('monthly_income')

    try:
        # Connect to the database
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor()

        update_query = "UPDATE user SET "
        update_values = []
        if current_balance is not None:
            update_query += "current_balance = %s, "
            update_values.append(float(current_balance))

        if monthly_income is not None:
            update_query += "monthly_income = %s, "
            update_values.append(float(monthly_income))
        # Remove trailing comma and space
        update_query = update_query[:-2]
        update_query += " WHERE id = %s"
        update_values.append(int(user_id))

        print(update_query , update_values) # UPDATE users SET current_balance = %s WHERE id = %s [1.0, 3]

        cursor.execute(update_query, tuple(update_values))
        connection.commit()

        cursor.close()
        connection.close()

        return jsonify({"message": "User updated successfully."}), 200
    except mysql.connector.Error as err:
        print(err)
        return jsonify({"error": str(err)}), 500


@app.route('/users_budgets', methods=['GET'])
def get_users_budgets():
    global budgets

    jwt_token = request.headers.get('Authorization')
    
    if not jwt_token:
        return jsonify({"error": "JWT token is missing."}), 400
    
    if jwt_token.startswith('Bearer '):
        jwt_token = jwt_token[7:]
    
    try:
        decoded_token = jwt.decode(jwt_token, SECRET_KEY, algorithms=['HS256'])
        username = decoded_token.get('username')
    except jwt.InvalidTokenError:
        return jsonify({"error": "Invalid JWT token."}), 400

    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor(dictionary=True)

        query = """
        SELECT u.username, u.current_balance, b.budget_amount, b.budget_name, b.budget_type
        FROM user u
        LEFT JOIN budget b ON u.id = b.user_id
        WHERE u.username = %s
        """
        cursor.execute(query, (username,))
        user_budgets = cursor.fetchall()
        budgets = user_budgets

        cursor.close()
        connection.close()

        return jsonify({"result": user_budgets}), 200
    except mysql.connector.Error as err:
        return jsonify({"error": str(err)}), 500
    
@app.route('/current_balance', methods=['GET'])
def get_current_balance():
    jwt_token = request.headers.get('Authorization')
    
    if not jwt_token:
        return jsonify({"error": "JWT token is missing."}), 400
    
    if jwt_token.startswith('Bearer '):
        jwt_token = jwt_token[7:]
    
    try:
        decoded_token = jwt.decode(jwt_token, SECRET_KEY, algorithms=['HS256'])
        username = decoded_token.get('username')

        user = get_user_by_username(username)
        
        if user:
          
            current_balance = user.get('current_balance', 0)

            return jsonify({"current_balance": str(current_balance)}), 200
        else:
            return jsonify({"error": "User not found."}), 404
    except jwt.ExpiredSignatureError:
        return jsonify({"error": "Expired token."}), 401
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/transaction_analysis', methods=['GET'])
def transaction_analysis():
    jwt_token = request.headers.get('Authorization')
    
    if not jwt_token:
        return jsonify({"error": "JWT token is missing."}), 400
    
    if jwt_token.startswith('Bearer '):
        jwt_token = jwt_token[7:]
    
    try:
        decoded_token = jwt.decode(jwt_token, SECRET_KEY, algorithms=['HS256'])
        username = decoded_token.get('username')

        user = get_user_by_username(username)
        
        if user:
            conn = mysql.connector.connect(**db_config)
            cursor = conn.cursor(dictionary=True)

            # Transaction Analysis
            cursor.execute('SELECT transaction_type, COUNT(*) as count FROM transaction WHERE user_id = %s GROUP BY transaction_type', (user['id'],))
            transaction_counts = cursor.fetchall()

            # Budget Analysis
            cursor.execute('SELECT budget_type, SUM(budget_amount) as amount FROM budget WHERE user_id = %s GROUP BY budget_type', (user['id'],))
            budget_counts = cursor.fetchall()
            conn.close()


            positive_count = 0
            negative_count = 0
            for tc in transaction_counts:
                if tc['transaction_type'] == 1:
                    positive_count = tc['count']
                elif tc['transaction_type'] == -1:
                    negative_count = tc['count']


            total_transactions = positive_count + negative_count
            positive_percentage = (positive_count / total_transactions) * 100 if total_transactions > 0 else 0
            negative_percentage = (negative_count / total_transactions) * 100 if total_transactions > 0 else 0


            labels_transactions = 'Positive Transactions', 'Negative Transactions'
            sizes_transactions = [positive_percentage, negative_percentage]
            fig1, ax1 = plt.subplots()
            ax1.pie(sizes_transactions, labels=labels_transactions, autopct='%1.1f%%', startangle=90)
            ax1.axis('equal')


            transaction_file_name = f'transaction_analysis.png'
            plt.savefig(f'../app/src/main/res/drawable/{transaction_file_name}')
            plt.close(fig1)


            budget_data = {}
            for bc in budget_counts:
                budget_data[bc['budget_type']] = bc['amount']

            fig2, ax2 = plt.subplots()
            ax2.pie(budget_data.values(), labels=budget_data.keys(), autopct='%1.1f%%', startangle=90)
            ax2.axis('equal')


            budget_file_name = f'budget_analysis.png'
            plt.savefig(f'../app/src/main/res/drawable/{budget_file_name}')
            plt.close(fig2)


            return jsonify({
                "message": "Successfully Made Analysis!"
            }), 200
        else:
            return jsonify({"error": "User not found."}), 404
    except jwt.ExpiredSignatureError:
        return jsonify({"error": "Expired token."}), 401
    except jwt.InvalidTokenError:
        return jsonify({"error": "Invalid token."}), 401
    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == "__main__":
    app.run(debug=True, port=APP_PORT, host=APP_HOST)
