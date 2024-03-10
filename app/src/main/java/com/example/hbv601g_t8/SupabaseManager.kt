package com.example.hbv601g_t8

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseManager {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://qcrqjzyoctyvhukzbodn.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFjcnFqenlvY3R5dmh1a3pib2RuIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTUwNDY5NTAsImV4cCI6MjAxMDYyMjk1MH0.nEQyU_7tzhOG9yUYe4zoYEOHX2llX3eFuM4mFdML3Gc"
    ) {
        install(Postgrest)
    }
}