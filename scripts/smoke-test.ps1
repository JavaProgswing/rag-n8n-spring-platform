[CmdletBinding()]
param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

$status = Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/v1/status"
if ($status.status -ne "ready") {
    throw "API status is not ready."
}

$document = @{
    title = "Smoke-test operations guide"
    source = "scripts/smoke-test.ps1"
    content = "For a priority one incident, acknowledge the alert within fifteen minutes. The incident commander coordinates recovery and records a timeline."
} | ConvertTo-Json

$ingested = Invoke-RestMethod `
    -Method Post `
    -Uri "$BaseUrl/api/v1/documents" `
    -ContentType "application/json" `
    -Body $document

$question = @{
    question = "Who coordinates recovery during a priority one incident?"
    topK = 3
    conversationId = "smoke-test"
} | ConvertTo-Json

$first = Invoke-RestMethod `
    -Method Post `
    -Uri "$BaseUrl/api/v1/query" `
    -ContentType "application/json" `
    -Body $question

$second = Invoke-RestMethod `
    -Method Post `
    -Uri "$BaseUrl/api/v1/query" `
    -ContentType "application/json" `
    -Body $question

if ($ingested.chunksCreated -lt 1) {
    throw "No chunks were created."
}
if ($first.sources.Count -lt 1 -or $first.answer -notmatch "incident commander") {
    throw "The answer was not grounded in the ingested document."
}
if (-not $second.cached) {
    throw "The repeated query was not served from cache."
}

[pscustomobject]@{
    Status = "PASS"
    DocumentId = $ingested.documentId
    Chunks = $ingested.chunksCreated
    Sources = $first.sources.Count
    RepeatQueryCached = $second.cached
}
