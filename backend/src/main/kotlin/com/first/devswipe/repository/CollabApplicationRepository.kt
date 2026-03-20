package com.first.devswipe.repository

import com.first.devswipe.entity.CollabApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CollabApplicationRepository : JpaRepository<CollabApplication, UUID> {
    fun findByCollabPostId(collabPostId: UUID): List<CollabApplication>
    fun findByApplicantId(applicantId: UUID): List<CollabApplication>
    fun findByCollabPostIdAndApplicantId(collabPostId: UUID, applicantId: UUID): CollabApplication?
    fun existsByCollabPostIdAndApplicantId(collabPostId: UUID, applicantId: UUID): Boolean
}
