
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
  const [input, setInput] = useState('')
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [code, setCode] = useState(`#include <iostream>
using namespace std;

int main() {
    // Type your code here...
}`)

  const [history, setHistory] = useState<Execution[]>([])


  const fetchHistory = async (pageNumber: number = 0) => {
    if (!import.meta.env.VITE_API_URL) {
      return
    }

    try {
      const response = await fetch(
        `${import.meta.env.VITE_API_URL}/api/code/history?page=${pageNumber}&size=10`
      )

      const data = await response.json()

      setHistory(data.content)
      setCurrentPage(data.number)
      setTotalPages(data.totalPages)
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
      setOutput('Submitting code...')

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
            input: input,
          }),
        }
      )

      if (!response.ok) {
        throw new Error('Code submission failed')
      }

      const result = await response.json()
      const jobId = result.jobId

      setOutput('Code submitted. Waiting for execution...')

      // Poll the status endpoint
      while (true) {
        await new Promise(resolve => setTimeout(resolve, 1000))

        const statusResponse = await fetch(
          `${import.meta.env.VITE_API_URL}/api/code/status/${jobId}`
        )

        if (!statusResponse.ok) {
          throw new Error('Unable to fetch execution status')
        }

        const statusResult = await statusResponse.json()

        if (
          statusResult.status === 'SUCCESS' ||
          statusResult.status === 'FAILED' ||
          statusResult.status === 'TIMEOUT'
        ) {
          if (statusResult.status === 'SUCCESS') {
            setOutput(
              statusResult.output || 'Program executed successfully (no output).'
            )
          } else {
            setOutput(
              statusResult.error || statusResult.output || 'Execution failed'
            )
          }

          await fetchHistory()
          break
        }
      }
    } catch (error) {
      setOutput('Unable to execute code or connect to the backend.')
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

          {/* Left: Code Editor */}

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

          {/* Right: Input and Terminal */}

          <div className="terminal-section">

            <div className="stdin-section">
              <h3>Program Input (stdin)</h3>

              <textarea
                className="stdin-input"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Enter program input..."
                rows={3}
              />
            </div>

            <div className="terminal-header">
              Terminal
            </div>

            <div className="output-panel">
              {output ? (
                <p>{output}</p>
              ) : (
                <p className="placeholder">
                  Output will appear here...
                </p>
              )}
            </div>

          </div>

        </div>

        {/* Execution History */}

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
        <div className="pagination">
          <button
            onClick={() => fetchHistory(currentPage - 1)}
            disabled={currentPage === 0}
          >
            Previous
          </button>

          <span>
            Page {currentPage + 1} of {totalPages}
          </span>

          <button
            onClick={() => fetchHistory(currentPage + 1)}
            disabled={currentPage >= totalPages - 1}
          >
            Next
          </button>
        </div>

      </main>
    </div>
  )
}

export default App
