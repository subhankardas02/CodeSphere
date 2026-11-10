import { useState } from 'react'
import './App.css'

function App() {
  const [output, setOutput]=useState('')
  return (
    <div className="app-container">

    <nav>
      <h1>CodeSphere</h1>
      <span>C++</span>
    </nav>
    <main>
      <h2>Code Editor</h2>
    <div className="editor-container">
      <p>Write your C++ code here...</p>
    </div>
    <button onClick={()=>setOutput('Hello from CodeSphere!')}>
      Run Code
    </button>
    <div>
      <h3>Output</h3>
      <p>{output}</p>
    </div>
    </main>
    </div>
  )
  
}

export default App
