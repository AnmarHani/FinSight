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
from constants import APP_HOST, APP_PORT

app = Flask(__name__)








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









@app.route('/generate_gemini_content', methods=['POST'])
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
        My primary financial goal is to save money, aiming for both short-term gains and long-term financial security. 
        I am interested in strategies for increasing my savings, managing expenses efficiently, and investing wisely with a focus on fault tolerance to match my goal.
        Please provide a detailed financial plan that includes:
        1. Strategies for allocating my current balance across different categories, emphasizing savings.
        2. A budget plan for my monthly income, with recommendations on spending, saving, and investing portions.
        3. Advice on managing expenses and recommendations for investment platforms or products that offer good fault tolerance.
        4. Any additional financial steps I should consider to improve my financial health and resilience against unforeseen financial challenges.
        """
    
    except jwt.ExpiredSignatureError:
        return jsonify({"error": "Expired token."}), 401
    except jwt.InvalidTokenError:
        return jsonify({"error": "Invalid token."}), 401
    
    api_key = "AIzaSyAyA2yr5nQv2QAdI1c6W1SMKyHZsYD3sxo"  
    try:
        generated_text = generate_gemini_content(prompt_text, api_key)
    except Exception as e:
        return jsonify({"error": "Failed to generate content.", "details": str(e)}), 500
    
    return jsonify({"generated_text": generated_text})

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
        SELECT t.transaction_id, t.amount, t.transaction_date, t.description, t.transaction_type
        FROM user u
        JOIN transaction t ON u.id = t.user_id
        WHERE u.username = %s
        """
        cursor.execute(query, (username,))
        user_transactions = cursor.fetchall()

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
    
@app.route('/users_budgets', methods=['GET'])
def get_users_budgets():
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
            transactions = get_user_transactions(user['id'])
            graph_html = generate_transaction_graph(transactions)

            return graph_html
        else:
            return jsonify({"error": "User not found."}), 404
    except jwt.ExpiredSignatureError:
        return jsonify({"error": "Expired token."}), 401


@app.route('/generate_gemini_finance_advice', methods=['POST'])
def generate_gemini_finance_advice():
    # Parse request data
    data = request.json
    prompt_text = data.get("prompt_text")

    
    if any(keyword in prompt_text.lower() for keyword in finance_keywords):
        # API key for the Gemini model
        api_key = "AIzaSyAyA2yr5nQv2QAdI1c6W1SMKyHZsYD3sxo"  # Replace with your actual API key

        # Generate content using Gemini API
        generated_text = generate_gemini_content(prompt_text, api_key)

        if generated_text:
            return jsonify({"finance_advice": generated_text})
        else:
            return jsonify({"error": "Failed to generate finance advice."}), 500
    else:
        return jsonify({"error": "Please provide a prompt related to money and finance."}), 400




if __name__ == "__main__":
    app.run(debug=True, port=APP_PORT, host=APP_HOST)
