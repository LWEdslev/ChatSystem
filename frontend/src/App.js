import React, { useEffect, useState, useRef } from 'react';

async function postMessage(message, username, setMessagesState) {
  try {
    const response = await fetch('/post-message', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ content: message, fromUsername: username })
    });

    if (!response.ok) {
      throw new Error('Failed to post message');
    }
    console.log('Message posted successfully');
    const messages = await getAllMessages();
    setMessagesState(messages);
  } catch (error) {
    console.error('Error posting message:', error.message);
  }
}

async function getAllMessages() {
  try {
    const response = await fetch('/get-all-messages');

    if (!response.ok) {
      throw new Error('Failed to fetch messages');
    }

    const messages = await response.json();
    console.log('All messages:', messages);
    return messages;
  } catch (error) {
    console.error('Error fetching messages:', error.message);
    return [];
  }
}

function jsonMessageToChatComponent(jsonMessage, username) {
  const { ID, content, fromUsername } = jsonMessage;
  if (fromUsername === username) {
    return <ChatRight key={ID}>
      <ChatBubble from={fromUsername} message={content} />
    </ChatRight>
  }
  return (<ChatLeft>
    <ChatBubble from={fromUsername} message={content} />
  </ChatLeft>)
}

function ChatBubble({ type, from, message }) {
  const ExtraClass = (type === "right") ? "chat-bubble-primary" : "";
  const alignmentClass = "items-start flex-row";

  return (
    <div>
      <div className={`flex ${alignmentClass} `}>
        <div className='chat-header ml-4 text-xl'>{from}</div>
      </div>
      <div>
        <div className={`chat-bubble max-w-3xl mr-2 ml-2 m-1 text-2xl ${ExtraClass}`}>{message}</div>
      </div>
    </div>
  );
}

function ChatLeft({ children }) {
  return (<div className={`chat`}>{children}</div>)
}

function ChatRight({ children }) {
  const rightChildren = React.Children.map(children, child =>
    React.cloneElement(child, { type: "right" })
  )
  return (<div className={`chat flex justify-end`}>{rightChildren}</div>)
}

function SendLine({ username, setMessagesState }) {
  const [messageState, setMessageState] = useState('');

  const handleKeyPress = (event) => {
    if (event.key === 'Enter') {
      sendMessage();
    }
  };

  const sendMessage = () => {
    postMessage(messageState, username, setMessageState).then(() => setMessageState(''));
    getAllMessages().then((messages) => setMessagesState(messages));
  };

  return (
    <div className="bottom-0 flex justify-center items-center w-full">
      <div className='join w-full flex h-16 mb-2 rounded-2xl'>
        <input
          type="text"
          placeholder="Enter message here"
          className="join-item input input-bordered border-2 w-full h-full text-2xl"
          value={messageState}
          onChange={(e) => setMessageState(e.target.value)}
          onKeyPress={handleKeyPress}
        />
        <button
          className='btn h-full join-item  h-full btn-primary text-xl'
          onClick={sendMessage}
        >
          Send
        </button>
      </div>
    </div>
  );
}

function Title() {
  return (
    <div className="flex justify-center items-center h-16 bg-base text-primary rounded-2xl mx-auto">
      <h1 className="text-2xl font-bold">Chat room</h1>
    </div>
  )
}

function ChatContainer({ children }) {
  const divRef = useRef(null);

  useEffect(() => {
    divRef.current.scrollIntoView({ behavior: 'smooth' });
  });

  return (
    <div className="flex justify-center overflow-hidden h-full ">
      <div className="flex flex-col flex-col-reverse p-1 mt-1 overflow-y-auto rounded-2xl w-full h-full border border-2 border-primary" style={{ maxHeight: 'calc(100vh - 140px)', WebkitOverflowScrolling: 'touch', scrollbarWidth: 'none', msOverflowStyle: 'none' }}>
        {React.Children.toArray(children).reverse()}
      </div>
      <div ref={divRef}></div>
    </div>
  )
}

function MainComponent({ children, username, setMessagesState}) {

  return (
    <div className="flex flex-col overflow-hidden h-screen w-3/4 mx-auto">
      <Title />
      <ChatContainer>{children}</ChatContainer>
      <SendLine 
        username={username}
        setMessagesState={setMessagesState}
      />
    </div>
  )
}

function Login({ usernameState, setUsernameState, hasLoggedInState, setHasLoggedInState, setMessagesState }) {
  const handleUsernameState = (e) => {
    setUsernameState(e.target.value)
  }

  const setLoginStateTrue = () => {
    if (usernameState !== '') {
      getAllMessages().then((messages) => setMessagesState(messages));
      document.removeEventListener('keypress', handleKeyPress);
      setHasLoggedInState(true)
    } 
  }

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') { setLoginStateTrue(); }
  };

  document.addEventListener('keypress', handleKeyPress);

  if (hasLoggedInState === true) { return null }

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 backdrop-blur">
      <div className="bottom-0 flex justify-center items-center">
        <div className='join w-full flex h-16 mb-2 rounded-2xl'>
          <input
            type="text"
            placeholder="Enter your username here"
            className="join-item input input-bordered border-2 w-full h-full text-2xl "
            value={usernameState}
            onChange={handleUsernameState}
          />
          <button className='btn h-full join-item  h-full btn-primary text-xl' onClick={setLoginStateTrue}>Login</button>
        </div>
      </div>
    </div>
  )
}

function ChatBox() {
  const [usernameState, setUsernameState] = useState('');
  const [hasLoggedInState, setHasLoggedInState] = useState(false);
  const [messagesState, setMessagesState] = useState([]);

  useEffect(() => {
    if (hasLoggedInState === true) {
      const interval = setInterval(() => {
        getAllMessages().then((messages) => setMessagesState(messages));
      }, 500);
      return () => clearInterval(interval);
    }
  }, [hasLoggedInState]);

  return (
    <MainComponent username={usernameState} setMessagesState={setMessagesState}>
      <Login
        usernameState={usernameState}
        setUsernameState={setUsernameState}
        hasLoggedInState={hasLoggedInState}
        setHasLoggedInState={setHasLoggedInState}
        setMessagesState={setMessagesState}
      />
      {messagesState.map((message) => jsonMessageToChatComponent(message, usernameState))}
    </MainComponent >
  );
}


export default ChatBox;
