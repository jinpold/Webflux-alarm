'use client'

import { useEffect, useState } from 'react';

interface NotificationEvent {
  data: string;
}

interface PostModel {
  title: string;
  content: string;
  authorId: string;
}

const Notifications = () => {
  const [notifications, setNotifications] = useState<string[]>([]);
  const [title, setTitle] = useState<string>('');
  const [content, setContent] = useState<string>('');

  useEffect(() => {
    const eventSource = new EventSource('http://localhost:8095/notifications/admin');

    eventSource.onmessage = function(event: NotificationEvent) {
      setNotifications(prevNotifications => [...prevNotifications, event.data]);
    };

    eventSource.onerror = function(err) {
      console.error("SSE 에러:", err);
      eventSource.close();
    };

    return () => {
      eventSource.close();
    };
  }, []);

  const handleTitleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTitle(e.target.value);
  };

  const handleContentChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setContent(e.target.value);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const post: PostModel = {
      title,
      content,
      authorId: 'admin' // 작성자 ID를 'admin'으로 설정
    };

    try {
      const response = await fetch('http://localhost:8095/posts/createPost', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(post),
      });

      if (!response.ok) {
        throw new Error('게시물 작성에 실패했습니다.');
      }

      // 성공적으로 게시물이 작성되면 제목과 내용을 초기화합니다.
      setTitle('');
      setContent('');

    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <div>
      <h1>실시간 알림</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="title">제목:</label>
          <input
            type="text"
            id="title"
            value={title}
            onChange={handleTitleChange}
          />
        </div>
        <div>
          <label htmlFor="content">내용:</label>
          <textarea
            id="content"
            value={content}
            onChange={handleContentChange}
          />
        </div>
        <button type="submit">공지 작성</button>
      </form>
      <ul>
        {notifications.map((notification, index) => (
          <li key={index}>{notification}</li>
        ))}
      </ul>
    </div>
  );
};

export default Notifications;
