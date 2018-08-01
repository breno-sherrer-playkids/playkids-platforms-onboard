<template>
<ul class="nav navbar-nav navbar-right">
    <!-- <router-link to="/" tag="li" exact>
      <a><i class="fa fa-bell"></i></a>
    </router-link> -->

    <li class="navbar-text" v-if="isLoggedIn">
      Welcome, <b>{{getUsername.value}}</b>!
    </li>

    <router-link to="/login" tag="li" exact v-if="!isLoggedIn">
      <a>Login</a>
    </router-link>

    <li v-else>
      <a href="#" @click="logout()">Logout</a>
    </li>

    <router-link to="/signup" tag="li" exact v-if="!isLoggedIn">
      <a>Create an account</a>
    </router-link>
</ul>
</template>

<script>
export default {
  data() {
    return {};
  },
  methods: {
    logout() {
      this.$authenticationService.logout();
      this.$router.push("/login");
    }
  },
  computed: {
    isLoggedIn() {
      return this.$store.state.logged;
    },
    getUsername() {
      return this.$store.state.username;
    }
  }
};
</script>
