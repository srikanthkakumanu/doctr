# Continuous Deployment (CD)

This repository supports Continuous Deployment using both Jenkins and GitHub Actions.

- **Jenkins Pipeline**: [Jenkinsfile](Jenkinsfile)
- **GitHub Actions Workflow**: [.github/workflows/ci-cd.yml](.github/workflows/ci-cd.yml)
- **Kubernetes manifests**: [k8s/deployment.yaml](k8s/deployment.yaml) and [k8s/service.yaml](k8s/service.yaml)
- **Deploy script**: [scripts/deploy.sh](scripts/deploy.sh)

## Jenkins CD

### Jenkins CD Configuration

The Jenkins pipeline includes a CD stage that deploys to Kubernetes when changes are pushed to the `main` branch.

### Prerequisites:

- Jenkins agent with `kubectl` configured for your Kubernetes cluster.
- DockerHub credentials set up in Jenkins (credential ID: `dockerhub-credentials`).
- Replace `your-dockerhub-username` in `Jenkinsfile` with your actual DockerHub username.

### Deployment Process:

1. Build and push Docker image to DockerHub.
2. Apply Kubernetes manifests using `kubectl apply -f k8s/`.

## GitHub Actions CD

### GitHub Actions CD Configuration

The GitHub Actions workflow deploys to Kubernetes after successful CI on the `main` branch.

### Prerequisites:

- Repository secrets: `DOCKERHUB_USERNAME`, `DOCKERHUB_PASSWORD`, `KUBE_CONFIG_DATA` (base64-encoded kubeconfig).
- Ensure `k8s/deployment.yaml` uses the correct image name.

### Deployment Process:

1. CI job builds and pushes Docker image.
2. CD job applies Kubernetes manifests.

## Manual Deployment

To deploy manually:

1. Build and push the Docker image:

   ```bash
   docker build -t your-dockerhub-username/doctr:latest .
   docker push your-dockerhub-username/doctr:latest
   ```

2. Deploy to Kubernetes:
   ```bash
   kubectl apply -f k8s/
   ```

Or use the deploy script:

```bash
./scripts/deploy.sh
```

## Notes

- Ensure the Kubernetes manifests are configured for your environment (e.g., correct image name, ports, environment variables).
- Adapt the pipeline and manifests to match your organization's policies (e.g., image scanning, approvals, staging environments).
