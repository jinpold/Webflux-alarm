'use client'
import React, { useEffect, useState } from 'react';

interface Notification {
  id: string;
  userId: string;
  adminId: string;
  message: string;
}

const AdminClient: React.FC = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);

  useEffect(() => {
    const eventSource = new EventSource('http://localhost:8091/api/notifications/admin');

    eventSource.onmessage = (event) => {
      const data: Notification = JSON.parse(event.data);
      setNotifications((prevNotifications) => [...prevNotifications, data]);
    };

    return () => {
      eventSource.close();
    };
  }, []);

  return (
    <div>
      <h1>Admin Client</h1>
      <ul>
        {notifications.map((notification) => (
          <li key={notification.id}>{notification.message}</li>
        ))}
      </ul>
    </div>
  );
};

export default AdminClient;