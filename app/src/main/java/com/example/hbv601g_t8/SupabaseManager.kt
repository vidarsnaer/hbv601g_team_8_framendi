package com.example.hbv601g_t8

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseManager {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://qxrmgqycgryhswziouye.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InF4cm1ncXljZ3J5aHN3emlvdXllIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTM0NDYzMTcsImV4cCI6MjAyOTAyMjMxN30.UN7kvW-gtCpp-hpgeta7EeIx8gLvPJj_qTgj7iIGvHc"
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
    }
}