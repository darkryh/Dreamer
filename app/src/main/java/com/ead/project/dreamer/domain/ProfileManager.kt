package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.apis.app.GetProfileScrap
import com.ead.project.dreamer.domain.databasequeries.*
import javax.inject.Inject

class ProfileManager @Inject constructor(
    val getProfile: GetProfile,
    val getProfileList: GetProfileList,
    val getProfilesToFix: GetProfilesToFix,
    val getLikedProfiles: GetLikedProfiles,
    val getMostViewedProfiles: GetMostViewedProfiles,
    val getProfileInboxRecommendations: GetProfileInboxRecommendations,
    val getProfilePlayerRecommendations: GetProfilePlayerRecommendations,
    val getProfilesReleases: GetProfilesReleases,
    val getProfilesFavoriteReleases: GetProfilesFavoriteReleases,
    val getProfileScrap: GetProfileScrap
)