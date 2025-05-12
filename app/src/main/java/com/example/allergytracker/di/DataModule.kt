package com.example.allergytracker.di

import android.content.Context
import com.example.allergytracker.data.local.LocalCache
import com.example.allergytracker.data.mapper.AllergyMapper
import com.example.allergytracker.data.mapper.ReactionMapper
import com.example.allergytracker.data.remote.datasource.FirebaseAllergyDataSource
import com.example.allergytracker.data.remote.datasource.FirebaseReactionDataSource
import com.example.allergytracker.data.remote.datasource.FirebaseSymptomDataSource
import com.example.allergytracker.data.repository.AllergyRepositoryImpl
import com.example.allergytracker.data.repository.ReactionRepositoryImpl
import com.example.allergytracker.data.repository.SymptomRepositoryImpl
import com.example.allergytracker.domain.repository.AllergyRepository
import com.example.allergytracker.domain.repository.ReactionRepository
import com.example.allergytracker.domain.repository.SymptomRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()
    
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
    
    @Provides
    @Singleton
    fun provideLocalCache(
        @ApplicationContext context: Context,
        gson: Gson
    ): LocalCache = LocalCache(context, gson)
    
    @Provides
    @Singleton
    fun provideAllergyDataSource(firestore: FirebaseFirestore): FirebaseAllergyDataSource =
        FirebaseAllergyDataSource(firestore)
    
    @Provides
    @Singleton
    fun provideReactionDataSource(firestore: FirebaseFirestore): FirebaseReactionDataSource =
        FirebaseReactionDataSource(firestore)
    
    @Provides
    @Singleton
    fun provideSymptomDataSource(firestore: FirebaseFirestore): FirebaseSymptomDataSource =
        FirebaseSymptomDataSource(firestore)
    
    @Provides
    @Singleton
    fun provideAllergyMapper(): AllergyMapper = AllergyMapper()
    
    @Provides
    @Singleton
    fun provideReactionMapper(): ReactionMapper = ReactionMapper()
    
    @Provides
    @Singleton
    fun provideAllergyRepository(
        dataSource: FirebaseAllergyDataSource,
        mapper: AllergyMapper
    ): AllergyRepository = AllergyRepositoryImpl(dataSource, mapper)
    
    @Provides
    @Singleton
    fun provideReactionRepository(
        dataSource: FirebaseReactionDataSource,
        mapper: ReactionMapper
    ): ReactionRepository = ReactionRepositoryImpl(dataSource, mapper)
    
    @Provides
    @Singleton
    fun provideSymptomRepository(
        dataSource: FirebaseSymptomDataSource
    ): SymptomRepository = SymptomRepositoryImpl(dataSource)
} 