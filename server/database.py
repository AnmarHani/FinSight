import mysql.connector
from mysql.connector import Error
import sqlite3
from sqlite3 import Error


class DatabaseManager:
    def __init__(self, db_name):
        self.conn = sqlite3.connect(db_name)
        self.cursor = self.conn.cursor()

    def execute_query(self, query, params=()):
        self.cursor.execute(query, params)
        self.conn.commit()

    def fetch_query(self, query, params=()):
        self.cursor.execute(query, params)
        return self.cursor.fetchone()

    def close_connection(self):
        self.cursor.close()
        self.conn.close()


def create_table(db, create_table_query):
    try:
        db.execute_query(create_table_query)
    except Error as e:
        print(f'Error: {e}')


def create_all_tables():
    database = "sqlite_db.db"

    sql_create_user_table = """CREATE TABLE IF NOT EXISTS user (
                                    id integer PRIMARY KEY AUTOINCREMENT,
                                    username text NOT NULL UNIQUE,
                                    email text NOT NULL UNIQUE,
                                    password text NOT NULL,
                                    current_balance real DEFAULT 0,
                                    monthly_income real,
                                    created_at text DEFAULT CURRENT_TIMESTAMP
                                );"""

    sql_create_transaction_table = """CREATE TABLE IF NOT EXISTS transaction (
                                        id integer PRIMARY KEY AUTOINCREMENT,
                                        user_id integer NOT NULL,
                                        amount real NOT NULL,
                                        transaction_date text NOT NULL,
                                        description text,
                                        transaction_type integer NOT NULL,
                                        FOREIGN KEY (user_id) REFERENCES user (id)
                                    );"""

    # create a database connection
    db = DatabaseManager(database)

    # create tables
    db.execute_query(sql_create_user_table)
    db.execute_query(sql_create_transaction_table)

    print("Tables created successfully!")

    db.close_connection()


def get_user_by_username(db, username):
    query = "SELECT * FROM user WHERE username = ?"
    return db.fetch_query(query, (username,))


def add_transaction_to_database(db, user_id, amount, transaction_date, description,
                                transaction_type):
    query = """
        INSERT INTO transaction (user_id, amount, transaction_date, description, transaction_type)
        VALUES (?, ?, ?, ?, ?)
    """
    db.execute_query(query, (user_id, amount, transaction_date, description, transaction_type))

    if transaction_type == 1:
        query = """
            UPDATE user
            SET current_balance = current_balance + ?
            WHERE id = ?
        """
        db.execute_query(query, (amount, user_id))
    elif transaction_type == -1:
        query = """
            UPDATE user
            SET current_balance = current_balance - ?
            WHERE id = ?
        """
        db.execute_query(query, (amount, user_id))


def username_exists(db, username):
    query = "SELECT username FROM user WHERE username = ?"
    return bool(db.fetch_query(query, (username,)))


def insert_user(db, username, email, password, current_balance=None, monthly_income=None):
    hashed_password = hash_password(password)
    query = "INSERT INTO user (username, email, password, current_balance, monthly_income) VALUES (?, ?, ?, ?, ?)"
    db.execute_query(query, (username, email, hashed_password, current_balance, monthly_income))


def get_user_transactions(db, user_id):
    query = "SELECT description, amount FROM transaction WHERE user_id = ?"
    return db.fetch_query(query, (user_id,))


def get_user_id_by_username(db, username):
    query = "SELECT id FROM user WHERE username = ?"
    return db.fetch_query(query, (username,))
