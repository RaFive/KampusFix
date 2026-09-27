package com.example.data.model

data class CampusBuilding(
    val name: String,
    val rooms: List<String>
)

object CampusData {
    val buildings = listOf(
        CampusBuilding(
            name = "Gedung Teknik",
            rooms = listOf("Ruang 203", "Ruang 204", "Lab Komputer 1", "Lab Komputer 2", "Bengkel Mekatronika", "Ruang Dosen Lt. 3")
        ),
        CampusBuilding(
            name = "Gedung Fakultas MIPA",
            rooms = listOf("Ruang 101", "Ruang 102", "Lab Fisika Dasar", "Lab Biologi", "Auditorium MIPA")
        ),
        CampusBuilding(
            name = "Gedung B",
            rooms = listOf("Koridor Lantai 1", "Koridor Lantai 2", "Ruang 201", "Ruang 202", "Toilet Lt. 2")
        ),
        CampusBuilding(
            name = "Gedung Rektorat",
            rooms = listOf("Lobby Utama", "Ruang Rapat Senat", "Pelayanan Akademik", "Biro Kemahasiswaan")
        ),
        CampusBuilding(
            name = "Perpustakaan Pusat",
            rooms = listOf("Area Baca Lt. 1", "Ruang Multimedia", "Ruang Diskusi 3A", "Toilet Barat Lt. 2")
        )
    )
}
