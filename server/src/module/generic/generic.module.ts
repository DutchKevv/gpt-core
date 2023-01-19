// import { ChatGPTAPIBrowser } from 'chatgpt'
import { Configuration, OpenAIApi } from 'openai'

export class GPTGeneric {

    openApi: OpenAIApi

    constructor() {

    }

    async init() {
        const configuration = new Configuration({
            apiKey: process.env.OPENAI_API_KEY,
        });

        this.openApi = new OpenAIApi(configuration);
    }

    async giveCommando(prompt: string) {
        try {
            const completion = await this.openApi.createCompletion({
                model: "text-davinci-003",
                prompt: prompt,
                max_tokens: 2000
            });

            console.log(completion.data.choices)

            return completion.data.choices[0].text
        } catch (error) {
            console.error(error)

            if (error?.response?.statusText) {
                return error.response.statusText
            }

            return 'ERROR'
        }
    }
}