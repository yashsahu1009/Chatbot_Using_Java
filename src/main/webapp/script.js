function sendMessage() {
    const userInput = document.getElementById('user-input').value;
    if (userInput.trim() === '') return;

    // Display user message
    displayMessage(userInput, 'user');

    // Prepare the URL with the query parameter
    const url = new URL('http://localhost:2025/ChatbotUsingJava/ChatbotServlet');
    const params = new URLSearchParams();
    params.append('query', userInput);
    url.search = params.toString();

    // Send the message to the backend (Servlet) using GET
    fetch(url, {
        method: 'GET',  // Use GET for the request
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
    })
    .then(response => {
        if (!response.ok) throw new Error('Network response was not ok');
        return response.json();
    })
    .then(data => {
        displayMessage(data.response, 'bot');
    })
    .catch(error => {
        displayMessage('Sorry, something went wrong. Please try again later.', 'bot');
        console.error('Error:', error);
    });

    // Clear the input field
    document.getElementById('user-input').value = '';
}

function displayMessage(message, sender) {
    const chatBox = document.getElementById('chat-box');

    const messageElement = document.createElement('div');
    messageElement.classList.add('chat-message', sender);

    const messageContent = document.createElement('p');
    messageContent.textContent = message;

    messageElement.appendChild(messageContent);
    chatBox.appendChild(messageElement);

    // Scroll to the bottom
    chatBox.scrollTop = chatBox.scrollHeight;
}
