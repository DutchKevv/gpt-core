import dotenv from 'dotenv'
import express, { Application } from 'express'
import cors from 'cors'
import bodyParser from 'body-parser'
import { GPTGeneric } from './module/generic/generic.module'

dotenv.config()

const app: Application = express()
const port = 3000

const gptGeneric = new GPTGeneric()
await gptGeneric.init()

app.use(bodyParser.json())
app.use(bodyParser.urlencoded())
app.use(cors())
app.use(express.static('public/client'))

app.get('/', (req, res) => {
  res.send('Hello World!')
})

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
});

// app.get('/api/test', async (req, res) => {
//   const prompt = 'Generate the source code for a fully functional todo app using JavaScript, CSS, and HTML. It should have a list of cat images'

//   console.log('prompt: ', prompt)
//   const html = await gptGeneric.giveCommando(prompt)

//   console.log(html)

//   res.send(html)
// });

app.post('/api/test', async (req, res) => {

  
  const prompt = `Generate the source code for a fully functional personal website, with CSS and full of color, about ${req.body.websiteAbout} . Using JavaScript, CSS, and HTML`

  console.log('prompt: ', prompt)
  const html = await gptGeneric.giveCommando(prompt)

  console.log(html)

  res.send({response: html})
})

