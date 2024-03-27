import bcrypt
from jose import JWTError, jwt

SECRET_KEY = "222"


def hash_password(password):
    salt = bcrypt.gensalt()
    hashed_password = bcrypt.hashpw(password.encode('utf-8'), salt)
    return hashed_password.decode('utf-8')


def verify_password(plain_password, hashed_password):
    return bcrypt.checkpw(plain_password.encode('utf-8'), hashed_password.encode('utf-8'))


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
