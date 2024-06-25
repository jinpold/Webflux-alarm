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
  const [inputMessage2, setInputMessage2] = useState<string>('');
  const messagesEndRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const eventSource = new EventSource(`http://localhost:8091/api/chats/recieve/1`);
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
        id: '2',
        roomId: "1",
        message: inputMessage,
        senderId: "667a63fcae8aee70a71123e6",
        senderName: "soo",
        createdAt: new Date().toISOString()
      };
      console.log("Sending message:", message);
      await fetch(`http://localhost:8091/api/chats/send`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(message),
      });
      setInputMessage('');
    }
  };

  const sendMessage2 = async () => {
    if (inputMessage2.trim()) {
      const message = {
        id: '3',
        roomId: "1",
        message: inputMessage2,
        senderId: "667a63fcae8aee70a71123e5",
        senderName: "jin",
        createdAt: new Date().toISOString()
      };
      console.log("Sending message:", message);
      await fetch(`http://localhost:8091/api/chats/send`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(message),
      });
      setInputMessage2('');
    }
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>, sendMessageFunction: () => void) => {
    if (e.key === 'Enter') {
      sendMessageFunction();
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
      <div style={{ position: 'fixed', height: '120px', bottom: 0, width: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ display: 'flex', flexDirection: 'row' }}>
          <input
            type="text"
            value={inputMessage}
            onKeyDown={(e) => handleKeyDown(e, sendMessage)}
            onChange={(e) => setInputMessage(e.target.value)}
            style={{ flex: 1 }}
            placeholder="soo의 메시지를 입력하세요"
          />
          <button onClick={sendMessage}>Send (soo)</button>
        </div>
        <div style={{ display: 'flex', flexDirection: 'row' }}>
          <input
            type="text"
            value={inputMessage2}
            onKeyDown={(e) => handleKeyDown(e, sendMessage2)}
            onChange={(e) => setInputMessage2(e.target.value)}
            style={{ flex: 1 }}
            placeholder="jin의 메시지를 입력하세요"
          />
          <button onClick={sendMessage2}>Send (jin)</button>
        </div>
      </div>
    </div>
  );
}
