'use client'
import React from 'react';

const UserClient: React.FC = () => {
  const sendSubscription = () => {
    fetch('http://localhost:8091/api/notifications', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        userId: 'user1',
        lawyerId: 'lawyer1',
        message: 'User1 has subscribed'
      })
    });
  };

  return (
    <div>
      <h1>User Client</h1>
      <button onClick={sendSubscription}>Subscribe</button>
    </div>
  );
};

export default UserClient;