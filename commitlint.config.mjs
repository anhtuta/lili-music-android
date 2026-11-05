export default {
  extends: ["@commitlint/config-conventional"],
  // Dependabot uses "Bump" (sentence-case), which fails subject-case.
  ignores: [(message) => /^chore\(deps\): Bump /u.test(message)],
};
