package com.unlone.app.android.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

data class RulesUiState(
    val loading: Boolean = false,
    val rules: List<String> = listOf(),
)

class RulesViewModel() : ViewModel() {

    val uiState = MutableStateFlow(RulesUiState())

    init {

        uiState.value = uiState.value.copy(rules = getRules())
    }

    fun getRules(): List<String> {
        return listOf(
            "Share genuine stories, but you may use fictitious names or metaphors in place of events and characters.",
            "Harming others is prohibited, including but not limited to slander, discriminatory, provocative, abusive, indecent words or personal attacks.",
            "Disclosure or threat of disclosure of any personal data or contact details is prohibited, such as communication profiles, social accounts, etc.",
            """
                No spamming, for exmaple
                    - Repeatedly posting the same content.
                    - Reposting the exact same content that someone else has already published.
            """.trimIndent(),
            "Unpleasant content such as pornography, revealing sensitive body parts, sexual harassment or gore is prohibited.  However, the content is not limited to those with educational purposes.",
            "Do not post content that promotes violence, harms others or animals, or encourages others to harm themselves.",
            "Harming non-adults by any form is prohibited, including those described above, undermining, stalking or harassing others by any form is prohibited.",
            "Content such as containing advertisements, commercial promotions is prohibited.",
            "Do not post vague or meaningless content. If the content prevents users from viewing other content, it will be removed and the poster may be punished.",
            "Distributing and discussing private pornographic content without the consent of the person involved is prohibited.",
            "Impersonating or misleading others into believing that you are any person or organization, or falsely representing or falsely claiming affiliation with any person or organization is prohibited.",
            "Fake news or news content that has been altered to mislead others is prohibited.",
            "It is prohibited to upload computer programs in any form intended to interfere with, destroy or restrict any software or hardware to this platform.",
            "Intentional or unintentional violation of any applicable local, national or international regulations, and any provisions of legal force is not allowed."

        )
    }

}