// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,

  mapbox: {
    accessToken:
      'pk.eyJ1IjoiYWRpbG90aGEiLCJhIjoiY2s2dnY4ZW5wMDFnejNucW9ncnF2c3hwcyJ9.-NzSatK_aFkiLpKXsd6BAA',
  },

  firebase: {
    apiKey: 'AIzaSyA5oDQrFKf_sjAAkBJzJFfE7kQcit2Hhgo',
    authDomain: 'wanderer-341922.firebaseapp.com',
    projectId: 'wanderer-341922',
    storageBucket: 'wanderer-341922.appspot.com',
    messagingSenderId: '381531134830',
    appId: '1:381531134830:web:9f48183efdc6b669cfff7f',
    measurementId: 'G-PJNEBC17EC',
  },

  googleMaps: 'AIzaSyBHkVH8NiHsuH4cU-GsZC38GXLvxoxXSjo',

  APIENDPOINT: 'http://localhost:8080/',

  OAUTH_REDIRECT_URL: 'http://localhost:8080/oauth2/authorization/google',
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
