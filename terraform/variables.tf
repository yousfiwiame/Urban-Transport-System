variable "project_id" {
  description = "GCP Project ID"
  type        = string
}

variable "region" {
  description = "GCP region"
  type        = string
  default     = "us-central1"
}

variable "cluster_name" {
  description = "GKE cluster name"
  type        = string
  default     = "urban-transport-cluster"
}

variable "environment" {
  description = "Environment (dev, staging, prod)"
  type        = string
  default     = "dev"
}

# Database variables
variable "db_instance_name" {
  description = "Cloud SQL instance name"
  type        = string
  default     = "urban-transport-db"
}

variable "db_password" {
  description = "Database root password"
  type        = string
  sensitive   = true
}

variable "redis_instance_name" {
  description = "Redis instance name"
  type        = string
  default     = "urban-transport-cache"
}

variable "mongodb_connection_string" {
  description = "MongoDB Atlas connection string"
  type        = string
  sensitive   = true
}

# Container image configuration
variable "image_registry" {
  description = "Container image registry"
  type        = string
  default     = "ghcr.io"
}

variable "image_prefix" {
  description = "Image prefix/namespace"
  type        = string
  default     = "yousfiwiame/urban-transport"
}

variable "image_tag" {
  description = "Container image tag"
  type        = string
  default     = "latest"
}
