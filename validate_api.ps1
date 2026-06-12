# Script de Validação Funcional da API - Mercado Inteligente
# Este script realiza chamadas básicas para garantir que os principais endpoints estão respondendo.

$baseUrl = "http://localhost:8080/api"

function Test-Endpoint {
    param($url, $description)
    Write-Host "Testando: $description ($url)... " -NoNewline
    try {
        $response = Invoke-RestMethod -Uri $url -Method Get -ErrorAction Stop
        Write-Host "OK!" -ForegroundColor Green
    } catch {
        Write-Host "FALHOU!" -ForegroundColor Red
        Write-Host "Erro: $($_.Exception.Message)"
    }
}

Write-Host "=== INICIANDO VALIDAÇÃO DA API ===" -ForegroundColor Cyan

# 1. Catálogo Público
Test-Endpoint "$baseUrl/produtos" "Listar Produtos"
Test-Endpoint "$baseUrl/categorias" "Listar Categorias"
Test-Endpoint "$baseUrl/marcas" "Listar Marcas"

# 2. Busca de Catálogo (Case Insensitive)
Test-Endpoint "$baseUrl/produtos/search?nome=a" "Busca de Produtos (nome=a)"
Test-Endpoint "$baseUrl/categorias/search?nome=a" "Busca de Categorias (nome=a)"
Test-Endpoint "$baseUrl/marcas/search?nome=a" "Busca de Marcas (nome=a)"

# 3. Documentação
Test-Endpoint "http://localhost:8080/v3/api-docs" "OpenAPI JSON"
Test-Endpoint "http://localhost:8080/swagger-ui.html" "Swagger UI"

Write-Host "=== VALIDAÇÃO CONCLUÍDA ===" -ForegroundColor Cyan
