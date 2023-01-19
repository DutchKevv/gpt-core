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

// app.get('/', (req, res) => {
//   res.send('Hello World!')
// })

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

app.post('/api/website', async (req, res) => {
  console.log(req.body)
  
  // const prompt = `Generate the source code for a fully functional personal website for a dog called Toby. It should look like a designer created it. Using CSS and full of color. Using JavaScript, and HTML`
  const prompt = `Create an html skeleton with bootstrap responsive design and responsive menu and responsive hamburger menu that works on mobile as well. And import all the necessary scripts before the closing body tag, such as jquery, popper and bootstrap. It has to embed a list of funny images of ${req.body.websiteAbout}. Convert all images to base64`
  // const prompt = `Create an html skeleton with bootstrap responsive design and responsive menu and responsive hamburger menu that works on mobile as well. And import all the necessary scripts before the closing body tag, such as jquery, popper and bootstrap. It has to show funny images of ${req.body.websiteAbout}. Convert all images to base64`
  // const prompt = `Generate the source code for a with . Using CSS, Javascript and HTML`

  console.log('prompt: ', prompt)
  const html = await gptGeneric.giveCommando(prompt)

  console.log(html)

  res.send({response: html})
})


app.post('/api/speak', async (req, res) => {
  const prompt = req.body.prompt

  console.log('prompt: ', prompt)
  const response = await gptGeneric.giveCommando(prompt)

  console.log(response)

  res.send({response})
})