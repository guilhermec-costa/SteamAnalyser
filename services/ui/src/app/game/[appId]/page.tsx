interface AppPageProps {
  params: {
    appId: string;
  }
}

export default function Page({params}: AppPageProps) {
  return (
    <div>{params.appId}</div>
  )
}