import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import AuthLogin from '@/components/auth/login/Login'
import AuthSignup from '@/components/auth/signup/Signup'
import UserBuyCredits from '@/components/user/Credits'
import UserBuyTicket from '@/components/user/BuyTicket'

Vue.use(Router)

const META_ONLY_AUTHENTICATED = 1;
const META_ONLY_NOT_AUTHENTICATED = 2;

const router = new Router({
  routes: [
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path: '/login',
      name: 'AuthLogin',
      component: AuthLogin,
      meta: META_ONLY_NOT_AUTHENTICATED
    },
    {
      path: '/signup',
      name: 'AuthSignup',
      component: AuthSignup,
      meta: META_ONLY_NOT_AUTHENTICATED
    },
    {
      path: '/buy-credits',
      name: 'UserBuyCredits',
      component: UserBuyCredits,
      meta: META_ONLY_AUTHENTICATED
    },
    {
      path: '/buy-ticket',
      name: 'UserBuyTicket',
      component: UserBuyTicket,
      meta: META_ONLY_AUTHENTICATED
    },
  ],
  linkActiveClass: 'active'
})

Vue.messenger = new Vue();
Vue.prototype.$messenger = Vue.messenger;

router.beforeEach((to, from, next) => {
  const authMeta = to.meta

  if (!authMeta) {
    next();
  }

  let logged = Vue.store.state.logged

  if ((authMeta == META_ONLY_AUTHENTICATED && !logged)
    || (authMeta == META_ONLY_NOT_AUTHENTICATED && logged)) {

    Vue.message.error("Sorry to disappoint you folk, but you can't do that :/")
    next({ path: from.path })
  } else {
    next();
  }
});

export default router;