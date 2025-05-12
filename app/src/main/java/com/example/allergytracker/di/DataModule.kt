package com.example.allergytracker.di

import com.example.allergytracker.data.local.dao.AllergyDao
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
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
        allergyDao: AllergyDao
    ): AllergyRepository = AllergyRepositoryImpl(dataSource, allergyDao)
    
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