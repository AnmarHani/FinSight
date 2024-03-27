from flask import Flask, request, jsonify

from pydantic import BaseModel

from typing import Optional
from decimal import Decimal
from datetime import datetime

from pydantic import BaseModel, ValidationError


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
