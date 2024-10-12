"use client"

import Image from "next/image";
import { useEffect, useState } from "react";

export default function Home() {
  const [data, setData] = useState();
  const fetchData = async () => {
    const result = await fetch("http://localhost:8080/steamCharts/mostPlayed")
    const json = await result.json();
    setData(json);
  }

  useEffect(() => {
    fetchData();
  }, [])

  return (
    <div>
      <p>
        {JSON.stringify(data)}
      </p>
    </div>
  );
}
