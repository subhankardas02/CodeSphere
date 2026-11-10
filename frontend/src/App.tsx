import { useEffect, useState } from 'react'
import Editor from '@monaco-editor/react'
import './App.css'
interface Execution {
  id: number;
  language: string;
  sourceCode: string;
  output: string;
  error: string;
  exitCode: number;
  status: string;
  createdAt: string;
}

function App() {
  const [output, setOutput] = useState('')
  const [code, setCode] = useState(`#include <iostream>
using namespace std;

int main() {
    // Type your code hear...
}`)
  const [history, setHistory] = useState<Execution[]>([])
  const fetchHistory = async () => {
    if (!import.meta.env.VITE_API_URL) {
  return
}
    try {
      const response = await fetch(
  `${import.meta.env.VITE_API_URL}/api/code/history`
)
      const data: Execution[] = await response.json()

      setHistory(data)
    } catch (error) {
      console.error('Failed to fetch execution history:', error)
    }
  }

  useEffect(() => {
    fetchHistory()
  }, [])

  const handleRun = async () => {
    if (!import.meta.env.VITE_API_URL) {
    setOutput('Code execution is not available in the public demo yet.')
    return
  }
    try {
      const response = await fetch(
  `${import.meta.env.VITE_API_URL}/api/code/submit`,
  {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          language: 'cpp',
          code: code,
        }),
      })

      const result = await response.json()

      setOutput(
        result.success
          ? result.output
          : result.error || result.message || 'Execution failed'
      )
      await fetchHistory()
    } catch (error) {
      setOutput('Unable to connect to the backend.')
    }
  }

  return (
    <div className="app-container">

      <nav>
        <h1>CodeSphere</h1>
        <span>C++</span>
      </nav>
      <main>
        <h2>Code Editor</h2>

        <div className="workspace">

          <div className="editor-section">
            <div className="editor-container">
              <Editor
                height="400px"
                language="cpp"
                theme="vs-dark"
                value={code}
                onChange={(value) => setCode(value ?? '')}
                options={{
                  fontSize: 15,
                  minimap: { enabled: false },
                  automaticLayout: true,
                  scrollBeyondLastLine: false,
                  padding: { top: 15 },
                }}
              />
            </div>

            <button onClick={handleRun}>
              Run Code
            </button>
          </div>

          <div className="terminal-section">
            <div className="terminal-header">
              Terminal
            </div>

            <div className="output-panel">
              {output ? (
                <p>{output}</p>
              ) : (
                <p className="placeholder">Output will appear here...</p>
              )}
            </div>
          </div>

        </div>
        <section className="history-section">
          <h2>Execution History</h2>

          {history.length === 0 ? (
            <p>No execution history available.</p>
          ) : (
            history.map((item) => (
              <div className="history-item" key={item.id}>
                <span>Execution #{item.id}</span>
                <span>{item.language}</span>
                <span>{item.status}</span>
              </div>
            ))
          )}
        </section>
      </main>
    </div>
  )

}

export default App
