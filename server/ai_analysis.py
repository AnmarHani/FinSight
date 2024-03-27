import requests
import plotly.graph_objects as go
import plotly.io as pio

finance_keywords = ["finance", "money", "investment", "budget", "income", "savings", "expenses"]


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


def generate_transaction_graph(transactions):
    categories = {}
    total_amount = 0

    # Calculate total amount and group transactions by category
    for transaction in transactions:
        total_amount += transaction['amount']
        category = categories.get(transaction['description'], 0)
        categories[transaction['description']] = category + transaction['amount']

    # Calculate percentage for each category
    category_percentages = {category: (amount / total_amount) * 100 for category, amount in
                            categories.items()}

    # Create the pie chart
    labels = list(category_percentages.keys())
    values = list(category_percentages.values())

    fig = go.Figure(data=[go.Pie(labels=labels, values=values)])
    fig.update_layout(title_text="Transaction Analysis")

    # Save the figure as a PNG image
    pio.write_image(fig, 'transaction_analysis.png')
