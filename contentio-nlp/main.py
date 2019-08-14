import sys
import nltk
from nltk.tokenize import word_tokenize
from nltk.tokenize import sent_tokenize
from flask import Flask, escape, request, jsonify

nltk.download('punkt')

app = Flask(__name__)


@app.route('/text-to-sentences', methods=["POST"])
def tts():
    data = request.get_json()
    text = data['text']

    word_count = len(word_tokenize(text))

    paragraphs = [p for p in text.split('\n\n') if p]
    paragraphed_sentences = []
    for index, paragraph in enumerate(paragraphs):
        sentences = sent_tokenize(paragraph)
        paragraphed_sentences += list(
            map(lambda s: {"text": s, "paragraph": index}, sentences))

    return jsonify({"sentences": paragraphed_sentences, "wordCount": word_count})


app.run(port=5000)
