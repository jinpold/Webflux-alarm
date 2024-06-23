'use client';
import { useEffect, useState, useRef, KeyboardEvent } from "react";

interface Notification {
  id: string;
  message: string;
  userId: string;
  response: string;
  adminId: string;
  status: string;
  createdAt: string;
}

export default function NotificationPage() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [inputMessage, setInputMessage] = useState<string>('');
  const notificationsEndRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const eventSource = new EventSource(`http://localhost:8091/api/notifications/admin`);
    eventSource.onopen = () => {
      console.log("SSE connection opened");
    };
    eventSource.onmessage = (event) => {
      try {
        const newNotification: Notification = JSON.parse(event.data);
        console.log("New notification", newNotification);
        setNotifications((prevNotifications) => [...prevNotifications, newNotification]);
      } catch (error) {
        console.error("Failed to parse notification", error);
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
    notificationsEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [notifications]);

  const sendNotification = async () => {
    if (inputMessage.trim()) {
      const notification = {
        id: '1',
        message: inputMessage,
        userId: "user123",
        response: "",
        adminId: "admin123",
        status: "NOTICE",
        createdAt: new Date().toISOString()
      };
      console.log("Sending notification:", notification);
      await fetch(`http://localhost:8091/api/notifications/subscribe`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(notification),
      });
      setInputMessage('');
    }
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      sendNotification();
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <h1>Notifications</h1>
      <div style={{ flex: 1, overflowY: 'auto', paddingBottom: '50px' }}>
        {notifications.map((notification, index) => (
          <p key={index}><strong>{notification.adminId}:</strong> {notification.message}</p>
        ))}
        <div ref={notificationsEndRef} />
      </div>
      <div style={{ position: 'fixed', height: '60px', bottom: 0, width: '100%', display: 'flex' }}>
        <input
          type="text"
          value={inputMessage}
          onKeyDown={handleKeyDown}
          onChange={(e) => setInputMessage(e.target.value)}
          style={{ flex: 1 }}
        />
        <button onClick={sendNotification}>Send</button>
      </div>
    </div>
  );
}
