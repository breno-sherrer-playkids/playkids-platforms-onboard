// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import VueResource from 'vue-resource'
import Element from './services/element'
import Vuex from 'vuex'

// Services
import AuthenticationService from './services/auth/authentication'
import UserService from './services/user/user'
import LotteryService from './services/lottery/lottery'
import TicketService from './services/ticket/ticket'

// API Root
const API = 'http://localhost:8081/api'

Vue.use(Vuex)
Vue.use(Element)

Vue.config.productionTip = false
Vue.use(VueResource)
Vue.http.options.root = API
Vue.router = router

Vue.use(AuthenticationService)
Vue.use(UserService)
Vue.use(LotteryService)
Vue.use(TicketService)

const store = new Vuex.Store({
  state: {
    logged: false,
    username: ''
  },
  mutations: {
    setLogged(state, value) {
      state.logged = value
    },
    setUsername(state, value) {
      state.username = value
    }
  },
  actions: {
    notifyLogin(context, username) {
      context.commit('setLogged', true)
      context.commit('setUsername', username)
    },
    notifyLogoff(context) {
      context.commit('setLogged', false)
    }
  }
})

Vue.store = store
Vue.prototype.$store = store

Vue.authenticationService.init()

Vue.http.interceptors.push((request, next) => {
  let token = localStorage.token

  if (token !== '' && request.root === Vue.http.options.root) {
    request.headers.set('Authorization', `Bearer ${token}`)
  }

  next()
})

Vue.http.interceptors.push(function (request, next) {
  next(function (res) {

    // Unauthorized Access
    if (res.status === 401 && 'Invalid credentials' === res.data) {

      Vue.authenticationService.logout()
      Vue.message.warning('Login expired')
      Vue.router.push('/login')
    }
  })
})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
