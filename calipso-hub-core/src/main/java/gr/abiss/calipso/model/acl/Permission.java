package gr.abiss.calipso.model.acl;

public class Permission {
	private Role role;
	private AccessMode accessMode;
	private Path path;

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public AccessMode getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(AccessMode accessMode) {
		this.accessMode = accessMode;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

}
