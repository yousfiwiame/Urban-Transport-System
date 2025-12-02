# Enabling Redis API
resource "google_project_service" "redis" {
  service            = "redis.googleapis.com"
  disable_on_destroy = false
}

# Redis (Memorystore) Instance
resource "google_redis_instance" "cache" {
  name               = var.redis_instance_name
  tier               = "BASIC"
  memory_size_gb     = 1
  region             = var.region
  redis_version      = "REDIS_7_0"
  display_name       = "Urban Transport Cache"
  
  # Network
  authorized_network = "default"
  connect_mode       = "DIRECT_PEERING"

  # Maintenance window
  maintenance_policy {
    weekly_maintenance_window {
      day = "SUNDAY"
      start_time {
        hours   = 3
        minutes = 0
      }
    }
  }

  depends_on = [google_project_service.redis]
}
