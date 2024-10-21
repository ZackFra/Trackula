import React from 'react'
import Tabs from '@/components/tabs/tabs';
import { getTimerEntries } from '@/api/timer';
import LoginForm from '@/components/login/login-form';

const Home = () => (

  <main className="flex justify-center flex-row">
    <LoginForm />
  </main>
)

// const Home = async () => {
//   const timerEntries = await getTimerEntries()
//   return (
//     <main className="flex justify-center flex-row">
//       <Tabs timerEntries={timerEntries} />
//     </main>
//   );
// }

export default Home
