import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'io.dutchapps.tellgpt',
  appName: 'TellGPT',
  webDir: 'www',
  bundledWebRuntime: false,
  server: {
    // url: "http://10.0.2.2:4200",
    cleartext: true
  }
};

export default config;
