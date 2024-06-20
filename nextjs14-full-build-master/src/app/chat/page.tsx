'use client';
import { useEffect, useState, useRef, KeyboardEvent } from "react";

interface Message {
  id: string;
  roomId: string;
  message: string;
  senderId: string;
  senderName: string;
  createdAt: string;
}

export default function ChatPage() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputMessage, setInputMessage] = useState<string>('');
  const messagesEndRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const eventSource = new EventSource(`http://localhost:8888/api/chat/recieve/1`);
    eventSource.onopen = () => {
      console.log("SSE connection opened");
    };
    eventSource.onmessage = (event) => {
      try {
        const newMessage: Message = JSON.parse(event.data);
        console.log("New message", newMessage);
        setMessages((prevMessages) => [...prevMessages, newMessage]);
      } catch (error) {
        console.error("Failed to parse message", error);
      }
    };
    eventSource.onerror = (e) => {
      console.error("SSE error", e);
      eventSource.close();
    };
    return () => {
      eventSource.close();
    };
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const sendMessage = async () => {
    if (inputMessage.trim()) {
      const message = {
        id: '1',
        roomId: "1",
        message: inputMessage,
        senderId: "66738fee2b75cb75fd7e4e8e",
        senderName: "User",
        createdAt: new Date().toISOString()
      };
      await fetch(`http://localhost:8888/api/chat/send`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(message),
      });
      setInputMessage('');
    }
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      sendMessage();
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <h1>Chat Room</h1>
      <div style={{ flex: 1, overflowY: 'auto', paddingBottom: '50px' }}>
        {messages.map((msg, index) => (
          <p key={index}><strong>{msg.senderName}:</strong> {msg.message}</p>
        ))}
        <div ref={messagesEndRef} />
      </div>
      <div style={{ position: 'fixed', height: '60px', bottom: 0, width: '100%', display: 'flex' }}>
        <input
          type="text"
          value={inputMessage}
          onKeyDown={handleKeyDown}
          onChange={(e) => setInputMessage(e.target.value)}
          style={{ flex: 1 }}
        />
        <button onClick={sendMessage}>Send</button>
      </div>
    </div>
  );
}
