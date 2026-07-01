<template>
	<Header />
	<div class="container about-page">
	  <h1>About <span class="highlight">SE</span><span>World</span></h1>
	  <p class="subtext">Empowering the next generation of Software Engineers</p>
	  
	  <p>
		<span class="highlight">SE</span><span>World</span> (codename) is a platform dedicated to supporting the teaching of 
		software engineering concepts. Our curriculum is aligned with the core topics of software engineering, ensuring 
		a comprehensive learning experience.
	  </p>
     
	  <h2>Topics Covered</h2>
	  <ul>
		<li>1. Requirements Engineering</li>
		<li>2. Software Specifications</li>
		<li>3. Black Box Testing</li>
	  </ul>
  
	  <p>
		Whether you are a student, educator, or enthusiast, <span class="highlight">SE</span><span>World</span> provides structured 
		resources and a hands-on approach to mastering these essential concepts.
	  </p>
	</div>
  </template>
  
  <script>
  import Header from "@/components/Header.vue";

  export default {
	components: { Header },
	name: "AboutPage",
  };
  </script>
  
  
<style scoped>
.about-page {
  text-align: center;
  padding: 5rem 2rem;
  color: #f8f9fa;
  align-items: center;
}

h1, h2 {
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 1.5rem;
}

.subtext {
  font-size: 1.4rem;
  font-style: italic;
  margin-top: 0.5rem;
  margin-bottom: 2rem;
}

p {
  font-size: 1.2rem;
  max-width: 900px; 
  margin: 0 auto;
  line-height: 1.8; 
  color: #d1d5db;
  margin-top: 2vh;
  margin-bottom: 2vh;
}

ul {
  list-style-type: none;
  padding: 0;
  margin: 2rem auto;
  max-width: 600px;
}

li {
  font-size: 1.4rem;
  font-weight: 500;
  color: #cbd5e1;
  margin-bottom: 1rem;
  padding: 0.5rem;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

li:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

/* Responsive adjustments */
@media (max-width: 768px) {
  h1 {
    font-size: 2rem;
  }
  
  h2 {
    font-size: 1.5rem;
  }
  
  p, li {
    font-size: 1rem;
  }
  
  .subtext {
    font-size: 1.1rem;
  }
}
  </style>
  