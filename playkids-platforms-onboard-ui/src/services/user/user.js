import VueResource from 'vue-resource';

const USER_PATH = 'user/';

export default {
  install(Vue) {
    Vue.use(VueResource)

    const userService = {

      consumeCongratulations() {

        this.congratulationsPromise = Vue.http.post(USER_PATH + 'congratulate')

        return this.congratulationsPromise
      },

      buyCredits(quantity) {
        this.buyCreditsPromise = Vue.http.post(USER_PATH + 'buy-credits', { quantity })

        return this.buyCreditsPromise
      }
    }

    Vue.userService = userService
    Vue.prototype.$userService = userService
  }
}