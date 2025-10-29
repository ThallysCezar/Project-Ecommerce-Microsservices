# 🚀 Guia de Deploy no Kubernetes (K3d) - E-Commerce Microsserviços

## 📋 Índice
1. [Pré-requisitos](#pré-requisitos)
2. [Configuração Inicial](#configuração-inicial)
3. [Build e Push das Imagens](#build-e-push-das-imagens)
4. [Deploy no Kubernetes](#deploy-no-kubernetes)
5. [Verificação e Testes](#verificação-e-testes)
6. [Comandos Úteis](#comandos-úteis)
7. [Troubleshooting](#troubleshooting)

---

## ✅ Pré-requisitos

### Ferramentas Instaladas:
- [x] **Docker Desktop** (rodando)
- [x] **K3d** instalado
- [x] **kubectl** instalado
- [x] **Conta Docker Hub** (criada e logada)

### Cluster K3d Criado:
```powershell
# Seu cluster já está criado:
k3d cluster list
# Deve mostrar: projectecommercemicro

# Ver nodes:
kubectl get nodes
```

---

## ⚙️ Configuração Inicial

### 1. Configurar Docker Hub Username

Edite o arquivo `k8s-build-push.ps1` e altere a linha:

```powershell
$DOCKER_USERNAME = "thallysprojetos"  # ⚠️ COLOQUE SEU USERNAME AQUI
```

### 2. Login no Docker Hub

```powershell
docker login
# Digite seu username e password
```

### 3. Verificar conectividade do cluster

```powershell
kubectl cluster-info
kubectl get nodes
kubectl get namespaces
```

---

## 🏗️ Build e Push das Imagens

### Passo 1: Executar script de build e push

```powershell
# Navegar para o diretório do projeto
cd c:\Workspace\ProjetosPessoais\Projeto-Ecommerce-Microsservico

# Executar script (pode demorar 10-15 minutos)
.\k8s-build-push.ps1
```

**O que o script faz:**
1. ✅ Verifica se Docker está rodando
2. ✅ Verifica se você está logado no Docker Hub
3. ✅ Faz build de todas as 8 imagens
4. ✅ Puxa as imagens para Docker Hub
5. ✅ Cria tags `latest` e `1.0.0` para cada imagem

### Passo 2: Verificar imagens locais

```powershell
.\k8s-verify-images.ps1
```

### Passo 3: Verificar no Docker Hub

Acesse: https://hub.docker.com/u/SEU-USERNAME

Você deve ver as 8 imagens:
- ✅ ecommerce-eureka-server
- ✅ ecommerce-api-gateway
- ✅ ecommerce-config-server
- ✅ ecommerce-ms-database
- ✅ ecommerce-ms-usuarios
- ✅ ecommerce-ms-produtos
- ✅ ecommerce-ms-pedidos
- ✅ ecommerce-ms-pagamentos

---

## 🎯 Deploy no Kubernetes

### Ordem de Deploy (IMPORTANTE!)

```
1. Namespace
2. ConfigMaps e Secrets
3. PostgreSQL (infraestrutura)
4. RabbitMQ (infraestrutura)
5. Eureka Server (service discovery)
6. ms-database (camada de dados)
7. Microsserviços (usuarios, produtos, pedidos, pagamentos)
8. API Gateway
9. Ingress (opcional)
```

### Passo 1: Criar Namespace

```powershell
kubectl apply -f k8s/namespace.yaml
kubectl get namespaces
```

### Passo 2: Criar ConfigMaps e Secrets

```powershell
kubectl apply -f k8s/configmaps/
kubectl apply -f k8s/secrets/
kubectl get configmaps -n ecommerce
kubectl get secrets -n ecommerce
```

### Passo 3: Deploy PostgreSQL

```powershell
kubectl apply -f k8s/infrastructure/postgres/
kubectl get pods -n ecommerce -w
# Aguardar até ficar Running
```

### Passo 4: Deploy RabbitMQ

```powershell
kubectl apply -f k8s/infrastructure/rabbitmq/
kubectl get pods -n ecommerce -w
# Aguardar até ficar Running
```

### Passo 5: Deploy Eureka Server

```powershell
kubectl apply -f k8s/discovery/
kubectl get pods -n ecommerce -w
# Aguardar até ficar Running
```

### Passo 6: Deploy ms-database

```powershell
kubectl apply -f k8s/microservices/ms-database/
kubectl get pods -n ecommerce -w
# Aguardar até ficar Running
```

### Passo 7: Deploy Microsserviços

```powershell
# Deploy todos de uma vez
kubectl apply -f k8s/microservices/ms-usuarios/
kubectl apply -f k8s/microservices/ms-produtos/
kubectl apply -f k8s/microservices/ms-pedidos/
kubectl apply -f k8s/microservices/ms-pagamentos/

# Monitorar
kubectl get pods -n ecommerce -w
```

### Passo 8: Deploy API Gateway

```powershell
kubectl apply -f k8s/gateway/
kubectl get pods -n ecommerce -w
```

### Passo 9: Verificar todos os serviços

```powershell
kubectl get all -n ecommerce
```

---

## ✅ Verificação e Testes

### 1. Verificar Pods

```powershell
kubectl get pods -n ecommerce

# Todos devem estar Running e Ready (1/1 ou 2/2)
```

### 2. Verificar Services

```powershell
kubectl get services -n ecommerce

# Verificar se todos os services têm ClusterIP
```

### 3. Verificar Logs

```powershell
# Ver logs de um pod específico
kubectl logs -n ecommerce <nome-do-pod>

# Exemplo:
kubectl logs -n ecommerce ecommerce-ms-usuarios-xxxxxxxxx-xxxxx

# Seguir logs em tempo real
kubectl logs -n ecommerce <nome-do-pod> -f
```

### 4. Acessar Eureka Dashboard

```powershell
# Port-forward para acessar localmente
kubectl port-forward -n ecommerce service/eureka-service 8081:8081

# Abrir no navegador:
# http://localhost:8081
```

### 5. Acessar API Gateway

```powershell
# Port-forward para acessar localmente
kubectl port-forward -n ecommerce service/api-gateway-service 8082:8082

# Testar no Insomnia/Postman:
# http://localhost:8082/ms-usuarios/...
```

### 6. Testar Autenticação

```powershell
# Criar usuário
POST http://localhost:8082/ms-usuarios/usuarios/criar
{
  "userName": "admin",
  "email": "admin@test.com",
  "password": "admin123",
  "role": "ADMIN"
}

# Login
POST http://localhost:8082/ms-usuarios/auth/login
{
  "email": "admin@test.com",
  "password": "admin123"
}
```

---

## 🛠️ Comandos Úteis

### Gerenciamento de Pods

```powershell
# Listar todos os pods
kubectl get pods -n ecommerce

# Detalhes de um pod
kubectl describe pod -n ecommerce <nome-do-pod>

# Entrar em um pod
kubectl exec -it -n ecommerce <nome-do-pod> -- /bin/sh

# Deletar um pod (será recriado automaticamente)
kubectl delete pod -n ecommerce <nome-do-pod>
```

### Gerenciamento de Deployments

```powershell
# Listar deployments
kubectl get deployments -n ecommerce

# Escalar um deployment
kubectl scale deployment -n ecommerce ms-usuarios --replicas=5

# Ver histórico de rollout
kubectl rollout history deployment -n ecommerce ms-usuarios

# Fazer rollback
kubectl rollout undo deployment -n ecommerce ms-usuarios
```

### Monitoramento

```powershell
# Ver uso de recursos
kubectl top nodes
kubectl top pods -n ecommerce

# Ver eventos
kubectl get events -n ecommerce --sort-by='.lastTimestamp'
```

### Limpeza

```powershell
# Deletar tudo do namespace
kubectl delete namespace ecommerce

# Deletar cluster K3d
k3d cluster delete projectecommercemicro

# Recriar cluster
k3d cluster create projectecommercemicro
```

---

## 🔧 Troubleshooting

### Problema: Pod não inicia (CrashLoopBackOff)

```powershell
# Ver logs do pod
kubectl logs -n ecommerce <nome-do-pod>

# Ver eventos do pod
kubectl describe pod -n ecommerce <nome-do-pod>

# Causas comuns:
# - Imagem não encontrada no Docker Hub
# - Variáveis de ambiente incorretas
# - Dependência não disponível (ex: PostgreSQL não está ready)
```

### Problema: Imagem não encontrada (ImagePullBackOff)

```powershell
# Verificar nome da imagem no deployment
kubectl get deployment -n ecommerce <nome> -o yaml | grep image

# Verificar se imagem existe no Docker Hub
# https://hub.docker.com/u/SEU-USERNAME

# Forçar re-pull
kubectl delete pod -n ecommerce <nome-do-pod>
```

### Problema: Serviços não se comunicam

```powershell
# Verificar DNS interno
kubectl exec -it -n ecommerce <nome-do-pod> -- nslookup postgres-service

# Testar conectividade
kubectl exec -it -n ecommerce <nome-do-pod> -- ping postgres-service

# Verificar variáveis de ambiente
kubectl exec -it -n ecommerce <nome-do-pod> -- env | grep DATABASE
```

### Problema: PostgreSQL não persiste dados

```powershell
# Verificar PersistentVolume
kubectl get pv
kubectl get pvc -n ecommerce

# Ver detalhes do volume
kubectl describe pvc -n ecommerce postgres-pvc
```

---

## 📊 Arquitetura Final

```
┌─────────────────────────────────────────────────────────┐
│          Kubernetes Cluster (K3d)                       │
│                                                         │
│  Namespace: ecommerce                                   │
│  ┌───────────────────────────────────────────────────┐ │
│  │  Ingress (opcional)                               │ │
│  │     ↓                                             │ │
│  │  API Gateway (LoadBalancer)                       │ │
│  │     ↓                                             │ │
│  │  Eureka Server (Service Discovery)                │ │
│  │     ↓                                             │ │
│  │  ┌─────────────────────────────────────────────┐ │ │
│  │  │  Microsserviços (3 réplicas cada)           │ │ │
│  │  │  - ms-usuarios                              │ │ │
│  │  │  - ms-produtos                              │ │ │
│  │  │  - ms-pedidos                               │ │ │
│  │  │  - ms-pagamentos                            │ │ │
│  │  └─────────────────────────────────────────────┘ │ │
│  │     ↓                                             │ │
│  │  ms-database (2 réplicas)                         │ │
│  │     ↓                                             │ │
│  │  ┌─────────────────────────────────────────────┐ │ │
│  │  │  Infraestrutura (StatefulSets)              │ │ │
│  │  │  - PostgreSQL (PersistentVolume)            │ │ │
│  │  │  - RabbitMQ (PersistentVolume)              │ │ │
│  │  └─────────────────────────────────────────────┘ │ │
│  └───────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

---

## 🎉 Próximos Passos

Depois que tudo estiver rodando:

1. ✅ Implementar Ingress (acesso externo via domínio)
2. ✅ Configurar HPA (auto-scaling)
3. ✅ Adicionar Prometheus + Grafana (monitoramento)
4. ✅ Implementar CI/CD com GitHub Actions
5. ✅ Deploy em cloud (AKS, EKS, GKE)

---

## 📚 Recursos Úteis

- [Documentação K3d](https://k3d.io/)
- [Documentação Kubernetes](https://kubernetes.io/docs/)
- [Docker Hub](https://hub.docker.com/)
- [kubectl Cheat Sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)

---

**Autor**: ThallysCezar  
**Projeto**: E-Commerce Microsserviços  
**Data**: 27/10/2025
