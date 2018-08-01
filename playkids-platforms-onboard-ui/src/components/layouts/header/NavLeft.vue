<template>
<ul class="nav navbar-nav">

    <router-link to="/buy-credits" tag="li" exact v-if="isLoggedIn">
      <a>Buy Credits</a>
    </router-link>

    <router-link to="/buy-ticket" tag="li" exact v-if="isLoggedIn">
      <a>Buy Ticket</a>
    </router-link>
</ul>
</template>

<script>
export default {
  computed: {
    isLoggedIn() {
      return this.$store.state.logged;
    }
  }
};
</script>
