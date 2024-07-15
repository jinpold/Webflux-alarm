"use client";

import axios from "axios";
import { useState, useEffect } from "react";

const NotificationAddPage = () => {
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [uploadedFiles, setUploadedFiles] = useState<{ id: string, filename: string }[]>([]);

  useEffect(() => {
    fetchUploadedFiles();
  }, []);

  const fetchUploadedFiles = async () => {
    try {
      const response = await axios.get("http://localhost:8095/file-record/files");
      setUploadedFiles(response.data);
    } catch (error) {
      console.error("파일 리스트 가져오기 오류:", error);
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      setSelectedFiles(Array.from(event.target.files));
    }
  };

  const uploadFiles = async () => {
    const formData = new FormData();

    if (selectedFiles.length === 0) return;

    selectedFiles.forEach(file => {
      formData.append("file", file);
    });

    try {
      const response = await axios.post(
        "http://localhost:8095/file-record/upload-files",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      setUploadedFiles(response.data);
    } catch (error) {
      console.error("파일 업로드 오류:", error);
    }
  };

  const downloadFile = async (fileId: string, filename: string) => {
    try {
      const response = await axios.get(
        `http://localhost:8095/file-record/files/${fileId}`,
        {
          responseType: "blob",
        }
      );

      const url = window.URL.createObjectURL(new Blob([response.data], { type: response.headers["content-type"] }));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error("파일 다운로드 오류:", error);
    }
  };

  const downloadAllFiles = async () => {
    try {
      const ids = uploadedFiles.map(file => file.id).join(',');
      const response = await axios.get(
        `http://localhost:8095/file-record/files/download?ids=${ids}`,
        {
          responseType: "blob",
        }
      );

      const url = window.URL.createObjectURL(new Blob([response.data], { type: response.headers["content-type"] }));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "files.zip");
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error("파일 다운로드 오류:", error);
    }
  };

  return (
    <div className="flex flex-col items-center pt-20">
      <div className="border border-black w-[50vw] p-10">
        <h1 className="text-[32px] border-b-2 p-4">파일 업로드 및 다운로드</h1>
        <input
          type="file"
          onChange={handleFileChange}
          className="mt-4"
          multiple
        />
        <input
          type="submit"
          value="업로드"
          onClick={uploadFiles}
          className="mt-4 bg-black text-white w-[45vw] h-[44px] rounded-xl"
        />
        <br />
        {uploadedFiles.length > 0 && uploadedFiles.map((file) => (
          <div key={file.id} className="flex items-center mt-4">
            <span className="mr-4">{file.filename}</span>
            <input
              type="submit"
              value="다운로드"
              onClick={() => downloadFile(file.id, file.filename)}
              className="bg-blue-500 text-white px-4 py-2 rounded"
            />
          </div>
        ))}
        <br />
        {uploadedFiles.length > 1 && (
          <input
            type="submit"
            value="모두 다운로드"
            onClick={downloadAllFiles}
            className="mt-4 bg-green-500 text-white w-[45vw] h-[44px] rounded-xl"
          />
        )}
      </div>
    </div>
  );
};

export default NotificationAddPage;
