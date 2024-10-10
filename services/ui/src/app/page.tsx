"use client"

import Image from "next/image";
import { useEffect } from "react";

export default function Home() {
  const fetchData = async () => {
    const result = await fetch("http://localhost:8080/user/stats?appId=730")
    const json = await result.json();
    console.log(json);
  }

  useEffect(() => {
    fetchData();
  }, [])

  return (
    <div>hello world</div>
  );
}
