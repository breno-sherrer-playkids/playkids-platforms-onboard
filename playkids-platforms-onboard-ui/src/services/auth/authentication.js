import VueResource from 'vue-resource';

const JWT_TOKEN = 'token';
const USERNAME = 'username';

const AUTHENTICATION_PATH = 'auth/';

export default {
  install(Vue) {
    Vue.use(VueResource)

    const userNotLoggedPromise = Promise.reject('User is not logged');
    userNotLoggedPromise.catch(() => { });

    const authenticationService = {
      loginPromise: userNotLoggedPromise,

      init() {
        this.token = window.localStorage.getItem(JWT_TOKEN)
        this.username = window.localStorage.getItem(USERNAME)

        if (this.token) {
          Vue.store.dispatch('notifyLogin', {
            value: this.username
          })
        }
      },

      signIn(username, password) {

        this.loginPromise = Vue.http.post(AUTHENTICATION_PATH + 'signin', { username, password })
          .then((response) => {
            this.token = response.body.token;
            this.username = username;

            window.localStorage.setItem(JWT_TOKEN, this.token)
            window.localStorage.setItem(USERNAME, this.username)

            Vue.store.dispatch('notifyLogin', {
              value: username
            })

            return this.token
          });

        return this.loginPromise;
      },

      signUp(username, password) {

        this.loginPromise = Vue.http.post(AUTHENTICATION_PATH + 'signup', { username, password })
          .then((response) => {
            this.token = response.body.token;
            this.username = username;

            window.localStorage.setItem(JWT_TOKEN, this.token)
            window.localStorage.setItem(USERNAME, this.username)

            Vue.store.dispatch('notifyLogin', {
              value: username
            })

            return this.token
          });

        return this.loginPromise;
      },

      logout() {
        localStorage.removeItem(JWT_TOKEN)
        localStorage.removeItem(USERNAME)

        this.username = null
        this.loginPromise = userNotLoggedPromise

        Vue.store.dispatch('notifyLogoff')
      },

      isLoggedIn() {
        return Vue.store.state.logged
      }
    };

    Vue.authenticationService = authenticationService;
    Vue.prototype.$authenticationService = authenticationService;
  },
};
