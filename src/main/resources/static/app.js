const statusEl = document.querySelector('#status');
const ingestForm = document.querySelector('#ingest-form');
const queryForm = document.querySelector('#query-form');

async function request(url, options = {}) {
  const response = await fetch(url, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  });
  const body = await response.json();
  if (!response.ok) throw new Error(body.detail || 'Request failed');
  return body;
}

async function refreshStatus() {
  try {
    const status = await request('/api/v1/status');
    statusEl.textContent = `Ready · ${status.documents} document${status.documents === 1 ? '' : 's'}`;
    statusEl.classList.add('ready');
  } catch {
    statusEl.textContent = 'API unavailable';
  }
}

ingestForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const button = ingestForm.querySelector('button');
  const result = document.querySelector('#ingest-result');
  button.disabled = true;
  result.textContent = 'Chunking and indexing…';
  try {
    const body = await request('/api/v1/documents', {
      method: 'POST',
      body: JSON.stringify({
        title: document.querySelector('#title').value,
        source: document.querySelector('#source').value,
        content: document.querySelector('#content').value
      })
    });
    result.textContent = `Indexed ${body.chunksCreated} chunk${body.chunksCreated === 1 ? '' : 's'}.`;
    await refreshStatus();
  } catch (error) {
    result.textContent = error.message;
  } finally {
    button.disabled = false;
  }
});

queryForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const button = queryForm.querySelector('button');
  const answer = document.querySelector('#answer');
  const sources = document.querySelector('#sources');
  button.disabled = true;
  answer.classList.remove('empty');
  answer.textContent = 'Retrieving relevant passages…';
  sources.replaceChildren();
  try {
    const body = await request('/api/v1/query', {
      method: 'POST',
      body: JSON.stringify({
        question: document.querySelector('#question').value,
        topK: Number(document.querySelector('#top-k').value)
      })
    });
    answer.textContent = body.answer + (body.cached ? '\n\nServed from cache.' : '');
    body.sources.forEach((source, index) => {
      const card = document.createElement('div');
      card.className = 'source';
      const heading = document.createElement('strong');
      heading.textContent = `[${index + 1}] ${source.title} · score ${source.score.toFixed(3)}`;
      const text = document.createElement('p');
      text.textContent = source.content;
      card.append(heading, text);
      sources.append(card);
    });
  } catch (error) {
    answer.textContent = error.message;
  } finally {
    button.disabled = false;
  }
});

refreshStatus();
