import React from "react";
import { Box, Text } from "rebass";
import { Link } from "react-router-dom";

const Home = () => {
  return (
    <Box bg="background" style={{ minHeight: "100vh" }}>
      <Box p={3} mx="auto" width={[1, 2 / 3, null, 2 / 5]}>
        <Link to="/projects">
          <Text>Projects</Text>
        </Link>
      </Box>
    </Box>
  );
};

export default Home;
