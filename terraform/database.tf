# Cloud SQL API
resource "google_project_service" "sqladmin" {
  service            = "sqladmin.googleapis.com"
  disable_on_destroy = false
}

# Cloud SQL PostgreSQL Instance
resource "google_sql_database_instance" "postgres" {
  name             = var.db_instance_name
  database_version = "POSTGRES_15"
  region           = var.region

  settings {
    # Free tier eligible
    tier = "db-f1-micro"

    # Backup configuration
    backup_configuration {
      enabled    = true
      start_time = "02:00"
    }

    # IP configuration
    ip_configuration {
      ipv4_enabled = true
    }

    # Maintenance window
    maintenance_window {
      day  = 7  # Sunday
      hour = 3
    }
  }

  deletion_protection = false

  depends_on = [google_project_service.sqladmin]
}

# Root user password
resource "google_sql_user" "root" {
  name     = "postgres"
  instance = google_sql_database_instance.postgres.name
  password = var.db_password
}

# Creating databases for each service
resource "google_sql_database" "userdb" {
  name     = "userdb"
  instance = google_sql_database_instance.postgres.name
}

resource "google_sql_database" "ticketdb" {
  name     = "ticketdb"
  instance = google_sql_database_instance.postgres.name
}

resource "google_sql_database" "scheduledb" {
  name     = "scheduledb"
  instance = google_sql_database_instance.postgres.name
}

resource "google_sql_database" "subscriptiondb" {
  name     = "subscriptiondb"
  instance = google_sql_database_instance.postgres.name
}
