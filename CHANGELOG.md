# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Example:
```
### Added
- for new features.
### Changed
- for changes in existing functionality.
### Deprecated
- for soon-to-be removed features.
### Removed
- for now removed features.
### Fixed
- for any bug fixes.
### Security
- in case of vulnerabilities.
```

## [Unreleased]

## [v0.1.0]
### Added
- Add GET API method /proxy-user/findByExtLogins
- Add GET API method /proxy-user/findByIdentifiers
- Add GET API method /proxy-user/findByPerunUserId
- Add GET API method /proxy-user/{login}
- Add GET API method /proxy-user/{login}/entitlements
- Add PUT API method /proxy-user/{login}/identity
- Add GET API method /relying-party/{rp-identifier}/proxy-user/{login}/entitlements

[v0.1.0]: https://github.com/CESNET/perun-proxy-api/commits/tree/v0.1.0
[Unreleased]: https://github.com/CESNET/perun-proxy-api/commits/master