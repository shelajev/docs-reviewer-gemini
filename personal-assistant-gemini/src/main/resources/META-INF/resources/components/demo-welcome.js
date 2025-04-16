import {LitElement, html, css} from 'lit';
import {state} from 'lit/decorators.js';

export class DemoWelcome extends LitElement {

    @state()
    private _query: string = '';

    @state()
    private _results: Array<{ title: string, fileId: string }> = [];

    static styles = css`
      h1 {
        font-family: "Red Hat Mono", monospace;
        font-size: 60px;
        font-style: normal;
        font-variant: normal;
        font-weight: 700;
        line-height: 26.4px;
        color: var(--main-highlight-text-color);
      }

      .title {
        text-align: center;
        padding: 1em;
        background: var(--main-bg-color);
      }
      
      .explanation {
        margin-left: auto;
        margin-right: auto;
        width: 50%;
        text-align: justify;
        font-size: 20px;
      }
      
      .explanation img {
        max-width: 60%;
        display: block;
        float:left;
        margin-right: 2em;
        margin-top: 1em;
      }
      
      .search-container {
          display: flex;
          justify-content: center;
          padding: 2em;
          gap: 0.5em;
      }

      .search-container input {
          padding: 0.5em;
          font-size: 1em;
          width: 40%;
      }

      .search-container button {
          padding: 0.5em 1em;
          font-size: 1em;
          cursor: pointer;
      }
      
      .results-container {
        margin-left: auto;
        margin-right: auto;
        width: 50%;
        padding: 1em;
      }
      
      .results-container ul {
          list-style: none;
          padding: 0;
      }
      
      .results-container li {
          background-color: var(--lumo-contrast-5pct);
          margin-bottom: 0.5em;
          padding: 0.8em;
          border-radius: 4px;
      }
      
      .results-container li a {
          text-decoration: none;
          color: var(--lumo-primary-text-color);
          cursor: pointer;
      }
      
      .results-container li a:hover {
          text-decoration: underline;
      }
    `
    
    private _handleInput(e: Event) {
        this._query = (e.target as HTMLInputElement).value;
    }

    private async _search() {
        if (!this._query.trim()) return;

        // Replace with actual backend call
        console.log("Searching for:", this._query);
        try {
            // Example: const response = await fetch('/api/search', { method: 'POST', body: JSON.stringify({ query: this._query }), headers: {'Content-Type': 'application/json'} });
            // const data = await response.json();
            // this._results = data;

            // Placeholder results
            this._results = [
                { title: "Document A.txt", fileId: "doc_a_123" },
                { title: "Meeting Notes B.docx", fileId: "notes_b_456" },
                { title: "Project Plan C.pdf", fileId: "plan_c_789" }
            ];

        } catch (error) {
            console.error("Search failed:", error);
            this._results = []; // Clear results on error
        }
    }
    
    private _handleKeyDown(e: KeyboardEvent) {
        if (e.key === 'Enter') {
            this._search();
        }
    }
    
    private _handleFileClick(fileId: string) {
        // Replace with actual navigation logic
        console.log("Navigate to file:", fileId);
        // Example: window.location.href = `/file/${fileId}`; 
    }

    render() {
        return html`
            <div class="title">
                <h1>Docs Reviewer Demo</h1>
            </div>
            
            <div class="explanation">
                <img src="images/chatbot-architecture.png"/>
                <p>
                    This application helps you review documents stored in your Google Drive. 
                    Enter a search query related to the documents you want to find. 
                    The system will use a Large Language Model (LLM) to search your Drive, 
                    retrieve relevant documents, and provide summaries or insights based on their content.
                </p>
            </div>
            
            <div class="search-container">
                <input 
                    type="text" 
                    placeholder="Enter your search query..." 
                    .value="${this._query}" 
                    @input="${this._handleInput}"
                    @keydown="${this._handleKeyDown}">
                <button @click="${this._search}">Search</button>
            </div>

            <div class="results-container">
                ${this._results.length > 0 ? html`
                    <ul>
                        ${this._results.map(result => html`
                            <li>
                                <a @click="${() => this._handleFileClick(result.fileId)}">
                                    ${result.title}
                                </a>
                            </li>
                        `)}
                    </ul>
                ` : ''}
            </div>
        `;
    }
}

customElements.define('demo-welcome', DemoWelcome);
