from flask import Flask, request, jsonify
from transformers import AutoTokenizer, AutoModel

# Define the Flask app
app = Flask(__name__)

# Define the embedding model
model_name = 'sentence-transformers/all-MiniLM-L6-v2'
tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModel.from_pretrained(model_name)

# Function to generate embeddings
def get_embeddings_for_text(text):
    inputs = tokenizer(text, return_tensors='pt', padding=True, truncation=True)
    outputs = model(**inputs)
    return outputs.last_hidden_state.mean(dim=1).detach().numpy().tolist()

# Define the route to generate embeddings
@app.route('/generate_embeddings', methods=['POST'])
def generate_embeddings():
    input_query = request.data.decode()
    print("Input query: ", input_query)
    embeddings = get_embeddings_for_text(input_query)
    return jsonify(embeddings)

# Run the Flask app
if __name__ == '__main__':
    app.run(debug=True)
