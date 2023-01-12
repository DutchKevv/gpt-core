// import { ChatGPTAPIBrowser } from 'chatgpt'
import { Configuration, OpenAIApi } from 'openai'

export class GPTGeneric {

    openApi: OpenAIApi

    constructor() {

    }

    async init() {
        console.log(process.env.OPENAI_API_KEY)

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
    
            console.log(completion.data.choices[0])
    
            return completion.data.choices[0].text
        } catch (error) {
            console.error(error)

            if (error?.response?.statusText) {
                return error.response.statusText
            }

            return 'ERROR'
        }
     
    }

    // async giveCommando2(prompt: string) {
    //     const api = new ChatGPTAPIBrowser({
    //         email: 'kewin@live.nl',
    //         // email: process.env.OPENAI_EMAIL as string,
    //         password: 'Sec#826|openai'
    //         // password: process.env.OPENAI_PASSWORD as string
    //     })

    //     await api.initSession()

    //     const res = await oraPromise(api.sendMessage(prompt), {
    //         text: prompt
    //     })
    //     console.log(res)

    //     // close the browser at the end
    //     await api.closeSession()
    // }
}